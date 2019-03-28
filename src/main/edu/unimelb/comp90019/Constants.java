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
}
