# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
import storm
from io import open
from vaderSentiment.vaderSentiment import SentimentIntensityAnalyzer

class VaderSentimentBolt(storm.BasicBolt):
    def process(self, tup):
    	vader_text = tup.values[11].encode('utf-8')
    	analyzer = SentimentIntensityAnalyzer()
        # analyzer.polarity_scores() returns the result in a dictionary
        # with the following keys 'compound', 'neg', 'neu', 'pos'
    	vs = analyzer.polarity_scores(vader_text)

        tuple = [tup.values[0], tup.values[1], tup.values[2], tup.values[3],
        	tup.values[4], tup.values[5], tup.values[6], tup.values[7],
        	tup.values[8], tup.values[9], tup.values[10], tup.values[11],
            tup.values[12], tup.values[13],
            vs['compound'], vs['neg'], vs['neu'], vs['pos']]

        storm.emit(tuple)

VaderSentimentBolt().run()
