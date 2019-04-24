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

/**
 * Application Constants
 *
 * @author Malcolm Chu
 * @version 0.1
 * @since 2019-03-22
 */
public class Constants {

    public static final String CONSUMER_KEY = "CONSUMER_KEY";
    public static final String CONSUMER_SECRET = "CONSUMER_SECRET";
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String ACCESS_TOKEN_SECRET = "ACCESS_TOKEN_SECRET";

    public static final int DEFAULT_WOEID = 1103816; // Melbourne
    public static final int LOCAL_CLUSTER_RUNTIME_IN_MINUTES = 5;
    public static final int REFRESH_TRENDS_IN_MINUTES = 15;
    public static final String MEDIA_TYPE = "photo";

    // @formatter:off
    /*
     * To authenticate with Twitter API:
     * 1. Use twitter4j.properties
     * 2. Use configuration builder and define the keys as constants
     *
     * ConfigurationBuilder cb = new ConfigurationBuilder();
     * cb.setDebugEnabled(true)
     * .setOAuthConsumerKey("")
     * .setOAuthConsumerSecret("")
     * .setOAuthAccessToken("")
     * .setOAuthAccessTokenSecret("")
     * .setTweetModeExtended(true);
     *
     * TwitterFactory tf = new TwitterFactory(cb.build());
     * Twitter twitter = tf.getInstance();
     */
    // @formatter:on

    public static final String ES_SERVER = "localhost";
    public static final String ES_PORT = "9200";
    // ES 6.7 - index:twitter, type:tweet
    // public static final String ES_INDEX = "twitter/tweet";
    // ES 7.0 - index:twitter-tweet (type has been removed)
    public static final String ES_INDEX = "twitter-tweet";
    public static final String ES_SUBMIT_BATCH_SIZE = "500";
    public static final int ES_SUBMIT_INTERVAL_IN_SECONDS = 15;
}
