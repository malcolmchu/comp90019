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
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import org.elasticsearch.storm.EsBolt;

import edu.unimelb.comp90019.bolt.ImageOcrBolt;
import edu.unimelb.comp90019.bolt.LangFilterBolt;
import edu.unimelb.comp90019.bolt.SanitizeBolt;
import edu.unimelb.comp90019.bolt.StanfordSentimentBolt;
import edu.unimelb.comp90019.bolt.VaderPySentimentBolt;
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

        Option woeid = new Option("i", "woeid", true,
                "sample tweets by place (woeid) trends, "
                        + "default = Melbourne (1103816)");
        woeid.setRequired(false);
        options.addOption(woeid);

        Option keyword = new Option("k", "keyword", true,
                "sample tweets by keywords, "
                        + "in quotes \"keyword1 keyword2 ... \"");
        keyword.setRequired(false);
        options.addOption(keyword);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;
        int inputWoeid = -1;
        String[] inputKeyword = null;

        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("woeid")) {
                inputWoeid = Integer.parseInt(cmd.getOptionValue("woeid"));
            }

            if (cmd.hasOption("keyword")) {
                inputKeyword = cmd.getOptionValue("keyword").split(" ");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            formatter.printHelp(
                    "storm jar edu.unimelb.comp90019.TwitterTopology", options);

            System.exit(1);
        }

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("twitterSpout",
                new TwitterSampleSpout(inputWoeid, inputKeyword), 1);

        // Text Processing Bolts
        builder.setBolt("langFilterBolt", new LangFilterBolt(), 2)
                .shuffleGrouping("twitterSpout");
        builder.setBolt("sanitizeBolt", new SanitizeBolt(), 2)
                .shuffleGrouping("langFilterBolt");
        builder.setBolt("stanfordSentimentBolt", new StanfordSentimentBolt(), 6)
                .shuffleGrouping("sanitizeBolt");
        builder.setBolt("vaderSentimentBolt", new VaderPySentimentBolt(), 2)
                .shuffleGrouping("stanfordSentimentBolt");
        // Image Processing Bolts
        builder.setBolt("imageOcrBolt", new ImageOcrBolt(), 2)
                .shuffleGrouping("langFilterBolt");

        // Define time and size trigger to submit results to ES
        // es.storm.bolt.flush.entries.size = X, submit to ES with X results
        // TOPOLOGY_TICK_TUPLE_FREQ_SECS = Y, submit to ES after Y seconds
        Map esBoltConf = new HashMap();
        esBoltConf.put("es.nodes", Constants.ES_SERVER);
        esBoltConf.put("es.port", Constants.ES_PORT);
        esBoltConf.put("es.storm.bolt.flush.entries.size",
                Constants.ES_SUBMIT_BATCH_SIZE);
        esBoltConf.put("es.storm.bolt.tick.tuple.flush", "true");

        // Specific settings for tweet bolt
        Map esBoltTweetConf = esBoltConf;
        esBoltTweetConf.put("es.mapping.id", TopologyFields.ID);

        builder.setBolt("esTweetBolt",
                new EsBolt(Constants.ES_TWEET_INDEX, esBoltTweetConf), 1)
                .shuffleGrouping("vaderSentimentBolt")
                .addConfiguration(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS,
                        Constants.ES_SUBMIT_INTERVAL_IN_SECONDS);

        // Specific settings for image bolt
        Map esBoltImageConf = esBoltConf;
        esBoltImageConf.put("es.mapping.id", TopologyFields.IMAGE_OCR_URL);

        builder.setBolt("esImageBolt",
                new EsBolt(Constants.ES_IMAGE_INDEX, esBoltImageConf), 1)
                .shuffleGrouping("imageOcrBolt")
                .addConfiguration(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS,
                        Constants.ES_SUBMIT_INTERVAL_IN_SECONDS);

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

            // Sleep for X mins before shutting down cluster
            Thread.sleep(
                    Constants.LOCAL_CLUSTER_RUNTIME_IN_MINUTES * 60 * 1000);

            cluster.shutdown();
        }
    }
}
