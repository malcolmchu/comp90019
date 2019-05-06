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

# Description: Sanitize tweet text using NLTK prior to sentiment analysis
# 
# Author: Malcolm Chu

import storm
from ttp import ttp
import re
import string
from nltk.tokenize import word_tokenize
from nltk.corpus import stopwords
from nltk.stem import WordNetLemmatizer
import emoji

# pip install twitter-text-python
# pip install nltk
# nltk.download()
# nltk.download('punkt')
# nltk.download('stopwords')
# nltk.download('wordnet')
# pip install emoji

class SanitizeBolt(storm.BasicBolt):
    def process(self, tup):
    	original_tweet = tup.values[3]

        ## Display Tweet ##
        p = ttp.Parser()
        result = p.parse(original_tweet)
        display_tweet = result.html
        
		## Common pre-processing ##
        preprocess_tweet = original_tweet
        # Remove RT
        preprocess_tweet = re.sub(r"RT @", "@", preprocess_tweet)
    	# Remove URL
    	preprocess_tweet = re.sub(r"http\S+", "", preprocess_tweet)
    	# Remove Hashtags
    	preprocess_tweet = re.sub(r"#\S+", "", preprocess_tweet)
    	# Remove Mentions
    	preprocess_tweet = re.sub(r"@\S+", "", preprocess_tweet)       
    	# Remove whitespaces
    	preprocess_tweet = re.sub("\s\s+" , " ", preprocess_tweet).strip()
    	
        ## NLTK pre-processing ##
        nltk_text = preprocess_tweet
        # Remove unicode characters (including emojis)
        nltk_text = nltk_text.encode('ascii', 'ignore').decode('ascii')
        # Remove punctuations and digits
        regex = re.compile('[%s]' % re.escape(string.punctuation))
        nltk_text = regex.sub('', nltk_text)
        regex = re.compile('[%s]' % re.escape(string.digits))
        nltk_text = regex.sub('', nltk_text)
        # Lowercase
        nltk_text = nltk_text.lower()
        # Tokenization
        words_token = word_tokenize(nltk_text)
        # Remove english stopwords
        words_nostop = [w for w in words_token if w not in stopwords.words('english')]
        # Lemmatization
        lemmatizer = WordNetLemmatizer()
        words_lemma = ' '.join([lemmatizer.lemmatize(w) for w in words_nostop])
        nltk_text = words_lemma
        
    	## StanfordNLP pre-processing ##
        # Convert emojis to text
        snlp_text = re.sub(r"[_:]", " ", preprocess_tweet) 
    	snlp_text = emoji.demojize(snlp_text)
        snlp_text = re.sub(r"_", " ", snlp_text)
        snlp_text = re.sub(r":", " \" ", snlp_text)
        snlp_text = re.sub("\s\s+" , " ", snlp_text).strip()
            	
    	## Vader pre-processing ##
    	vader_text = preprocess_tweet

        tuple = [tup.values[0], tup.values[1], tup.values[2], tup.values[3],
        	tup.values[4], tup.values[5], tup.values[6], tup.values[7],
        	tup.values[8], tup.values[9], display_tweet,
            snlp_text, vader_text, nltk_text]

        storm.emit(tuple)

SanitizeBolt().run()
