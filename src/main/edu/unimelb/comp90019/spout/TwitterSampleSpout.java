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
package edu.unimelb.comp90019.spout;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.storm.Config;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.joda.time.DateTime;

import edu.unimelb.comp90019.TopologyFields;
import twitter4j.FilterQuery;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.URLEntity;

/**
 * Twitter Sample Spout
 *
 * @author Malcolm Chu
 * @version 0.1
 * @since 2019-03-22
 */
@SuppressWarnings("serial")
public class TwitterSampleSpout extends BaseRichSpout {
    SpoutOutputCollector collector;
    LinkedBlockingQueue<Status> queue = null;

    String keyWords[];
    TwitterStream twitterStream;

    public TwitterSampleSpout(String[] keyWords) {
        this.keyWords = keyWords;
    }

    public TwitterSampleSpout() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void open(Map conf, TopologyContext context,
            SpoutOutputCollector collector) {
        this.queue = new LinkedBlockingQueue<Status>(1000);
        this.collector = collector;
        StatusListener listener = new StatusListener() {

            @Override
            public void onStatus(Status status) {
                queue.offer(status);
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice sdn) {
            }

            @Override
            public void onTrackLimitationNotice(int i) {
            }

            @Override
            public void onScrubGeo(long l, long l1) {
            }

            @Override
            public void onException(Exception e) {
            }

            @Override
            public void onStallWarning(StallWarning warning) {
            }

        };

        twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(listener);
        if (keyWords == null) {
            twitterStream.sample();
        } else {
            FilterQuery query = new FilterQuery().track(keyWords);
            twitterStream.filter(query);
        }
    }

    @Override
    public void nextTuple() {
        Status ret = queue.poll();

        if (ret == null) {
            Utils.sleep(50);
        } else {
            String tweet_text = "";

            // Note that for retweets you have to use
            // getRetweetedStatus().getText() instead of just .getText()
            if (ret.isRetweet()) {
                tweet_text = "RT @"
                        + ret.getRetweetedStatus().getUser().getScreenName()
                        + ": " + ret.getRetweetedStatus().getText();
            } else {
                tweet_text = ret.getText();
            }

            // Convert java.util.Date to org.joda.time.DateTime
            // Joda time will output date in ISO8601 format (ES Compatibility)
            DateTime dt = new DateTime(ret.getCreatedAt());

            String hashtags = "";
            String expanded_urls = "";
            String media_urls = "";

            // Hashtags
            for (HashtagEntity he : ret.getHashtagEntities()) {
                hashtags += he.getText() + TopologyFields.DELIMITER;
            }

            // Expanded URLs (Links)
            for (URLEntity ue : ret.getURLEntities()) {
                expanded_urls += ue.getExpandedURL() + TopologyFields.DELIMITER;
            }

            // Media URLs (Photos)
            for (MediaEntity me : ret.getMediaEntities()) {
                if (me.getType().equals("photo")) {
                    media_urls += me.getMediaURLHttps()
                            + TopologyFields.DELIMITER;
                }
            }

            collector.emit(new Values(ret.getId(), ret.getLang(),
                    ret.getUser().getScreenName(), tweet_text,
                    ret.getFavoriteCount(), dt.toString(), hashtags,
                    expanded_urls, media_urls));
        }
    }

    @Override
    public void close() {
        twitterStream.shutdown();
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Config conf = new Config();
        conf.setMaxTaskParallelism(1);
        return conf;
    }

    @Override
    public void ack(Object id) {
    }

    @Override
    public void fail(Object id) {
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(TopologyFields.ID, TopologyFields.LANG,
                TopologyFields.USER_SCREEN_NAME, TopologyFields.TWEET_TEXT,
                TopologyFields.FAV_COUNT, TopologyFields.CREATED_AT,
                TopologyFields.HASHTAGS, TopologyFields.EXPANDED_URLS,
                TopologyFields.MEDIA_URLS));
    }
}
