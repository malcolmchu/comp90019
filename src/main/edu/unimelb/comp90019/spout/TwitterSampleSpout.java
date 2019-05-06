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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
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

import edu.unimelb.comp90019.Constants;
import edu.unimelb.comp90019.TopologyFields;
import twitter4j.FilterQuery;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
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

    int woeid = -1;
    String[] keywords;
    ArrayList<String> filter;
    Twitter twitter;
    Trends trends;
    TwitterStream twitterStream;

    public TwitterSampleSpout(int woeid, String[] keywords) {
        this.woeid = woeid > 0 ? woeid : Constants.DEFAULT_WOEID;
        this.keywords = keywords;
    }

    public TwitterSampleSpout() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void open(Map conf, TopologyContext context,
            SpoutOutputCollector collector) {
        this.queue = new LinkedBlockingQueue<Status>(1000);
        this.collector = collector;

        final StatusListener listener = new StatusListener() {

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

        twitter = new TwitterFactory().getInstance();

        // Create a timer to trigger defined action immediately (0) and schedule
        // subsequent action as defined by
        // (Constants.REFRESH_INTERVAL_IN_MINUTES)
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Get trends by place (woeid)
                try {
                    trends = twitter.getPlaceTrends(woeid);
                    filter = new ArrayList<String>();

                    for (Trend trend : trends.getTrends()) {
                        filter.add(trend.getName());
                    }

                    if (keywords != null) {
                        for (String keyword : keywords) {
                            filter.add(keyword);
                        }
                    }
                } catch (TwitterException te) {
                    te.printStackTrace();
                    System.out.println(
                            "Failed to get trends: " + te.getMessage());
                    System.exit(-1);
                }

                // Shutdown the twitter stream if it was created before
                if (twitterStream != null) {
                    twitterStream.shutdown();
                }

                twitterStream = new TwitterStreamFactory().getInstance();
                twitterStream.addListener(listener);

                // If filter (trends) is empty, sample without filtering
                if (filter.size() > 0) {
                    FilterQuery query = new FilterQuery()
                            .track(filter.toArray(new String[filter.size()]));
                    twitterStream.filter(query);

                    System.out.println("Filtering twitter streams with: "
                            + Arrays.toString(filter.toArray()));
                } else {
                    twitterStream.sample();
                }
            }
        }, 0, Constants.REFRESH_TRENDS_IN_MINUTES * 60 * 1000);
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
            // Joda time will output date in ISO8601 format
            // This is necessary as ES only accepts date format in ISO8601
            DateTime dt = new DateTime(ret.getCreatedAt());

            String hashtags = "";
            String expanded_urls = "";
            String media_urls = "";
            String location = null;

            // Hashtags
            for (HashtagEntity he : ret.getHashtagEntities()) {
                hashtags += he.getText() + TopologyFields.DELIMITER;
            }
            hashtags = hashtags.trim();

            // Expanded URLs (Links)
            for (URLEntity ue : ret.getURLEntities()) {
                expanded_urls += ue.getExpandedURL() + TopologyFields.DELIMITER;
            }
            expanded_urls = expanded_urls.trim();

            // Media URLs (Photos)
            for (MediaEntity me : ret.getMediaEntities()) {
                if (me.getType().equals(Constants.MEDIA_TYPE)) {
                    media_urls += me.getMediaURLHttps()
                            + TopologyFields.DELIMITER;
                }
            }
            media_urls = media_urls.trim();

            // Location is only available for original tweets and not retweets
            // https://developer.twitter.com/en/docs/tweets/filter-realtime/guides/basic-stream-parameters.html#locations
            if (ret.getGeoLocation() != null) {
                location = String.valueOf(ret.getGeoLocation().getLatitude())
                        + ","
                        + String.valueOf(ret.getGeoLocation().getLongitude());
                System.out.println("geolocation: " + location);
            }

            collector.emit(new Values(ret.getId(), ret.getLang(),
                    ret.getUser().getScreenName(), tweet_text,
                    ret.getFavoriteCount(), dt.toString(), hashtags,
                    expanded_urls, media_urls, location));
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
                TopologyFields.MEDIA_URLS, TopologyFields.LOCATION));
    }
}
