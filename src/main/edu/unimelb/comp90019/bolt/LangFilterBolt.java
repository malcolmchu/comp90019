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
package edu.unimelb.comp90019.bolt;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import edu.unimelb.comp90019.TopologyFields;

/**
 * Language Filter Bolt
 *
 * @author Malcolm Chu
 * @version 0.1
 * @since 2019-03-22
 */
public class LangFilterBolt implements IRichBolt {
    private OutputCollector collector;

    @Override
    public void prepare(Map conf, TopologyContext context,
            OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple tuple) {
        if (tuple.getValueByField(TopologyFields.LANG).equals("en")) {
            // @formatter:off
            collector.emit(new Values(
                    tuple.getValueByField(TopologyFields.ID),
                    tuple.getValueByField(TopologyFields.LANG),
                    tuple.getValueByField(TopologyFields.USER_SCREEN_NAME),
                    tuple.getValueByField(TopologyFields.TWEET_TEXT),
                    tuple.getValueByField(TopologyFields.FAV_COUNT),
                    tuple.getValueByField(TopologyFields.CREATED_AT),
                    tuple.getValueByField(TopologyFields.HASHTAGS),
                    tuple.getValueByField(TopologyFields.EXPANDED_URLS),
                    tuple.getValueByField(TopologyFields.MEDIA_URLS),
                    tuple.getValueByField(TopologyFields.LOCATION)));
            // @formatter:on
        }
    }

    @Override
    public void cleanup() {
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(TopologyFields.ID, TopologyFields.LANG,
                TopologyFields.USER_SCREEN_NAME, TopologyFields.TWEET_TEXT,
                TopologyFields.FAV_COUNT, TopologyFields.CREATED_AT,
                TopologyFields.HASHTAGS, TopologyFields.EXPANDED_URLS,
                TopologyFields.MEDIA_URLS, TopologyFields.LOCATION));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
