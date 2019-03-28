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

import org.apache.storm.task.ShellBolt;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;

import edu.unimelb.comp90019.TopologyFields;

/**
 * Vader Sentiment Bolt
 *
 * @author Malcolm Chu
 * @version 0.1
 * @since 2019-03-24
 */
public class VaderSentimentBolt extends ShellBolt implements IRichBolt {
    public VaderSentimentBolt() {
        super("python", "vader.py");
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(TopologyFields.ID, TopologyFields.LANG,
                TopologyFields.USER_SCREEN_NAME, TopologyFields.TWEET_TEXT,
                TopologyFields.FAV_COUNT, TopologyFields.CREATED_AT,
                TopologyFields.HASHTAGS, TopologyFields.EXPANDED_URLS,
                TopologyFields.MEDIA_URLS, TopologyFields.DISPLAY_TWEET,
                TopologyFields.STANFORD_TEXT, TopologyFields.VADER_TEXT,
                TopologyFields.NLTK_TEXT, TopologyFields.STANFORD_SCORE,
                TopologyFields.VADER_SCORE_COMPOUND,
                TopologyFields.VADER_SCORE_NEGATIVE,
                TopologyFields.VADER_SCORE_NEUTRAL,
                TopologyFields.VADER_SCORE_POSITIVE));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
