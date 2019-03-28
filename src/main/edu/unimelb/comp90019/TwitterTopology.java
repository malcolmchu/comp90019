/*
 * {{ COMP90019 }}
 * Copyright (C) {{ 2019 }}  {{ University of Melbourne }}
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package edu.unimelb.comp90019;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import org.elasticsearch.storm.EsBolt;

import edu.unimelb.comp90019.bolt.LangFilterBolt;
import edu.unimelb.comp90019.bolt.SanitizeBolt;
import edu.unimelb.comp90019.bolt.StanfordSentimentBolt;
import edu.unimelb.comp90019.bolt.VaderSentimentBolt;
import edu.unimelb.comp90019.spout.TwitterSampleSpout;

/**
 * Twitter Storm Topology
 *
 * @author Malcolm Chu
 * @version 0.1
 * @since 2019-03-22
 */
public class TwitterTopology {

    public static void main(String[] args) throws Exception {

        Options options = new Options();

        Option topologyID = new Option("t", "topologyID", true, "topology id");
        topologyID.setRequired(false);
        options.addOption(topologyID);

        Option keywords = new Option("k", "keywords", true,
                "\"multiple keywords in quotes\"");
        keywords.setRequired(true);
        options.addOption(keywords);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp(
                    "storm jar edu.unimelb.comp90019.TwitterTopology", options);

            System.exit(1);
        }

        TopologyBuilder builder = new TopologyBuilder();

        if (cmd.hasOption("keywords")) {
            builder.setSpout("twitterSpout", new TwitterSampleSpout(
                    cmd.getOptionValue("keywords").split(" ")), 1);
        } else {
            builder.setSpout("twitterSpout", new TwitterSampleSpout(), 1);
        }

        builder.setBolt("langFilterBolt", new LangFilterBolt(), 4)
                .shuffleGrouping("twitterSpout");
        builder.setBolt("sanitizeBolt", new SanitizeBolt(), 4)
                .shuffleGrouping("langFilterBolt");
        builder.setBolt("stanfordSentimentBolt", new StanfordSentimentBolt(), 2)
                .shuffleGrouping("sanitizeBolt");
        builder.setBolt("vaderSentimentBolt", new VaderSentimentBolt(), 4)
                .shuffleGrouping("stanfordSentimentBolt");

        // Define time and size trigger to submit results to ES
        // es.storm.bolt.flush.entries.size = 500
        // TOPOLOGY_TICK_TUPLE_FREQ_SECS = 15
        Map esBoltConf = new HashMap();
        esBoltConf.put("es.storm.bolt.flush.entries.size", "500");
        esBoltConf.put("es.storm.bolt.tick.tuple.flush", "true");
        esBoltConf.put("es.mapping.id", "id");

        builder.setBolt("esBolt", new EsBolt("twitter/tweet", esBoltConf), 2)
                .shuffleGrouping("vaderSentimentBolt")
                .addConfiguration(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 15);

        Config topologyConf = new Config();
        topologyConf.setDebug(true);

        if (cmd.hasOption("topologyID")) {
            topologyConf.setNumWorkers(3);

            StormSubmitter.submitTopologyWithProgressBar(
                    cmd.getOptionValue("topologyID"), topologyConf,
                    builder.createTopology());
        } else {
            topologyConf.setMaxTaskParallelism(3);

            // Test the topology on a local cluster
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("twitter", topologyConf,
                    builder.createTopology());

            // Sleep for a min
            Thread.sleep(60000);

            cluster.shutdown();
        }
    }
}
