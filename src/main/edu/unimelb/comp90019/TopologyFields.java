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
 * Topology Fields
 *
 * @author Malcolm Chu
 * @version 0.1
 * @since 2019-03-25
 */
public class TopologyFields {
    public static final String DELIMITER = " ";

    // Twitter4J fields
    public static final String ID = "id";
    public static final String LANG = "lang";
    public static final String USER_SCREEN_NAME = "user_screen_name";
    public static final String TWEET_TEXT = "tweet_text";
    public static final String FAV_COUNT = "favorite_count";
    public static final String CREATED_AT = "created_at";
    public static final String HASHTAGS = "hashtags";
    public static final String EXPANDED_URLS = "expanded_urls";
    public static final String MEDIA_URLS = "media_urls";
    public static final String LOCATION = "location";

    // System generated fields
    public static final String DISPLAY_TWEET = "display_tweet";
    public static final String STANFORD_TEXT = "stanford_text";
    public static final String VADER_TEXT = "vader_text";
    public static final String NLTK_TEXT = "nltk_text";

    // Stanford NLP generated fields
    public static final String STANFORD_SCORE = "snlp_score";

    // Vader generated fields
    public static final String VADER_SCORE_COMPOUND = "vs_compound";
    public static final String VADER_SCORE_NEGATIVE = "vs_negative";
    public static final String VADER_SCORE_NEUTRAL = "vs_neutral";
    public static final String VADER_SCORE_POSITIVE = "vs_positive";

}
