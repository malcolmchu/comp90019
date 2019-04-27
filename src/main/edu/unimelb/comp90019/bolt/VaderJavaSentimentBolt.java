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

import java.io.IOException;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import com.vader.sentiment.analyzer.SentimentAnalyzer;

import edu.unimelb.comp90019.TopologyFields;

/**
 * Vader Sentiment Bolt in Java
 *
 * Source: https://github.com/apanimesh061/VaderSentimentJava
 *
 * @author Malcolm Chu
 * @version 0.1
 * @since 2019-04-26
 */
public class VaderJavaSentimentBolt implements IRichBolt {
    private OutputCollector collector;

    @Override
    public void prepare(Map conf, TopologyContext context,
            OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple tuple) {
        String vaderText = (String) tuple
                .getValueByField(TopologyFields.VADER_TEXT);

        SentimentAnalyzer sentimentAnalyzer;
        Map<String, Float> vaderScore;
        try {
            sentimentAnalyzer = new SentimentAnalyzer(vaderText);
            // @formatter:off
            /*
             * Exception in thread "main" java.lang.IllegalAccessError: tried to access method org.apache.lucene.analysis.TokenStream.<init>()V from class com.vader.sentiment.processor.InputAnalyzer
             *      at com.vader.sentiment.processor.InputAnalyzer.tokenize(InputAnalyzer.java:61)
             *      at com.vader.sentiment.processor.InputAnalyzer.removePunctuation(InputAnalyzer.java:99)
             *      at com.vader.sentiment.processor.TextProperties.setWordsOnly(TextProperties.java:114)
             *      at com.vader.sentiment.processor.TextProperties.setWordsAndEmoticons(TextProperties.java:80)
             *      at com.vader.sentiment.processor.TextProperties.<init>(TextProperties.java:69)
             *      at com.vader.sentiment.analyzer.SentimentAnalyzer.setInputStringProperties(SentimentAnalyzer.java:106)
             *      at com.vader.sentiment.analyzer.SentimentAnalyzer.<init>(SentimentAnalyzer.java:93)
             */
            // @formatter:on
            sentimentAnalyzer.analyze();
            vaderScore = sentimentAnalyzer.getPolarity();

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
                    tuple.getValueByField(TopologyFields.LOCATION),
                    tuple.getValueByField(TopologyFields.DISPLAY_TWEET),
                    tuple.getValueByField(TopologyFields.STANFORD_TEXT),
                    tuple.getValueByField(TopologyFields.VADER_TEXT),
                    tuple.getValueByField(TopologyFields.NLTK_TEXT),
                    tuple.getValueByField(TopologyFields.STANFORD_SCORE),
                    vaderScore.get("compound"), vaderScore.get("neg"),
                    vaderScore.get("neu"), vaderScore.get("pos")));
            // @formatter:on
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
                TopologyFields.MEDIA_URLS, TopologyFields.LOCATION,
                TopologyFields.DISPLAY_TWEET, TopologyFields.STANFORD_TEXT,
                TopologyFields.VADER_TEXT, TopologyFields.NLTK_TEXT,
                TopologyFields.STANFORD_SCORE,
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
