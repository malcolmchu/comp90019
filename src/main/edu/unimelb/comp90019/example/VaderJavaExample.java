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
package edu.unimelb.comp90019.example;

import java.io.IOException;
import java.util.ArrayList;

import com.vader.sentiment.analyzer.SentimentAnalyzer;

/**
 * Vader Sentiment Example in Java
 *
 * Source: https://github.com/apanimesh061/VaderSentimentJava
 *
 * @author Malcolm Chu
 * @version 0.1
 * @since 2019-04-26
 */
public class VaderJavaExample {
    public static void main(String[] args) throws IOException {
        ArrayList<String> sentences = new ArrayList<String>() {
            {
                add("Unimelb is smart, handsome, and funny.");
                add("Unimelb is smart, handsome, and funny!");
                add("Unimelb is very smart, handsome, and funny.");
                add("Unimelb is VERY SMART, handsome, and FUNNY.");
                add("Unimelb is VERY SMART, handsome, and FUNNY!!!");
                add("Unimelb is VERY SMART, uber handsome, and FRIGGIN FUNNY!!!");
                add("Unimelb is not smart, handsome, nor funny.");
                add("The book was good.");
                add("At least it isn't a horrible book.");
                add("The book was only kind of good.");
                add("The plot was good, but the characters are uncompelling and the dialog is not great.");
                add("Today SUX!");
                add("Today only kinda sux! But I'll get by, lol");
                add("Make sure you :) or :D today!");
                add("Catch utf-8 emoji such as 💘 and 💋 and 😁");
                add("Catch utf-8 emoji such as \"heart with arrow\" and \"kiss mark\" and \"beaming face with smiling eyes\"");
            }
        };

        for (String sentence : sentences) {
            System.out.println(sentence);
            SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer(
                    sentence);
            sentimentAnalyzer.analyze();
            System.out.println(sentimentAnalyzer.getPolarity());
        }
    }
}
