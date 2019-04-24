# COMP90019
The purpose of this project is to mine opinions from Twitter by collecting tweets in real-time over a distributed streaming platform. Sentiment analysis are applied on text found in tweets in conjunction with non-textual information e.g. emojis and images, that are peculiar in microblogging platforms. Tweets from trending topics are collected using the Twitter Streaming API, pre-processed, analysed and then centrally stored for retrospective analysis. Through data augmentation, by using additional features from non-textual information, we have built a real-time distributed analytics platform that performs opinion mining on Twitter data which can be extended to any other social media platform.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

Download the project source code from Github and compile it using Maven. The following must be installed to build the project using Maven.

* Java SDK 1.7 or 1.8 [link](https://www.java.com/en/download/)
* Maven 3.6 [link](https://maven.apache.org/install.html)

Below are the general steps required to setup a Storm cluster [link](http://storm.apache.org/releases/1.2.2/Setting-up-a-Storm-cluster.html):

1. Set up a Zookeeper cluster [link](http://zookeeper.apache.org/doc/r3.3.3/zookeeperAdmin.html)
2. Install dependencies on Nimbus and worker machines
3. Download and extract a Storm release to Nimbus and worker machines
4. Fill in mandatory configurations into storm.yaml *(not required on a single machine cluster)*
5. Launch daemons under supervision using "storm" script and a supervisor of your choice

For each worker machine on the Storm cluster, the following must be installed:
* Apache Storm 1.2.2 [link](https://storm.apache.org/downloads.html)
* Python 2.7 AND 3.6 [link](https://www.python.org/downloads/)
* Python 2.7 dependencies and NLTK
* Python 3.6 dependencies and Tesseract 4

Setup twitter credentials as follows:
1. Rename `config\twitter4j.properties.empty` to `config\twitter4j.properties`
2. Register for application tokens using a Twitter developer account [(click here for details)](https://developer.twitter.com/en/docs/basics/authentication/guides/access-tokens.html)
3. Copy the `consumerKey`, `consumerSecret`, `accessToken`, `accessTokenSecret` into `twitter4j.properties`  before packaging the project for deployment
```
debug=true
oauth.consumerKey=<insert-consumer-key-here>
oauth.consumerSecret=<insert-consumer-secret-here>
oauth.accessToken=<insert-access-token-here>
oauth.accessTokenSecret=<insert-access-token-secret-here>
tweetModeExtended=true
```

Download and unpack Elasticsearch and Kibana for your distribution
* Elasticsearch 7.0 [link](https://www.elastic.co/downloads/elasticsearch)
* Kibana 7.0 [link](https://www.elastic.co/downloads/kibana)

Ensure that the version of `elasticsearch-storm` dependency inside the `pom.xml` build file is the same or greater than the Elasticsearch version installed. Otherwise, EsBolt will throw a version exception.
```
<!-- https://mvnrepository.com/artifact/org.elasticsearch/elasticsearch-storm -->
<dependency>
    <groupId>org.elasticsearch</groupId>
    <artifactId>elasticsearch-storm</artifactId>
    <version>7.0.0</version>
</dependency>
```

### Installing Dependencies

Note that all dependencies must be installed on each worker machine on the Storm cluster. We use `pip install` to install packages into Python 2 and `pip3 install` for Python 3. Ensure that this is the same for your environment or adjust the installation procedures accordingly. Python 2 scripts are executed with `python <script-name>` and Python 3 scripts are executed with `python3 <script-name>`. Python scripts are located in `multilang/resources` under the project folder and are executed by Storm bolts as follows:

```java
public class SanitizeBolt extends ShellBolt implements IRichBolt {
    public SanitizeBolt() {
        /* Execute sanitize.py in Python 2 */
        super("python", "sanitize.py");
        /* Execute sanitize.py in Python 3 */
        //super("python3", "sanitize.py");
    }
    
    ...
}
```

1. Install Python 2.7 dependencies
    * *twitter-text-python* [link](https://pypi.org/project/twitter-text-python/): parse twitter text and convert them to html for display
    * *emoji* [link](https://pypi.org/project/emoji/): convert emojis into text descriptions
    * *vaderSentiment* [link](https://pypi.org/project/vader-sentiment/): analyse sentiment polarity (of tweet) using vader
    * *wget* [link](https://pypi.org/project/wget/): download tweet images using wget
    ```
    sudo pip install twitter-text-python
    sudo pip install emoji
    sudo pip install vaderSentiment
    sudo pip install wget
    ```

    **Important**:
    * vaderSentiment.py has been ported to python 3 and must be modified to work with python 2.7
    * Add "from io import open" to vaderSentiment.py
    <https://github.com/cjhutto/vaderSentiment/issues/47>

2. Install NLTK [link](https://www.nltk.org/install.html):
    ```
    sudo pip install -U nltk
    ```

    1. Launch the python interpreter over the command line.
        ```
        MacBook-Pro:comp90019 malcolmchu$ python
        Python 2.7.10 (default, Feb 22 2019, 21:17:52) 
        [GCC 4.2.1 Compatible Apple LLVM 10.0.1 (clang-1001.0.37.14)] on darwin
        Type "help", "copyright", "credits" or "license" for more information.
        >>>
        ```

    2. Below are NLTK packages that has to be installed via the python interpreter:
        * *models/punkt*: Punkt Tokenizer Models
        * *copora/stopwords*: Stopwords Corpus
        * *copora/wordnet*: WordNet
        ```python
        >>> import nltk
        >>> nltk.download('punkt')
        >>> nltk.download('stopwords')
        >>> nltk.download('wordnet')
        ```

3. Install Python 3.6 dependencies
    * *opencv-contrib-python* [link](https://pypi.org/project/opencv-contrib-python/): Pre-built OpenCV packages for Python
    * *imutils* [link](https://pypi.org/project/imutils/): Series of convenience functions to make basic image processing functions
    * *pillow* [link](https://pypi.org/project/Pillow/): Read all image types supported by the Python Imaging Library e.g. jpeg, png, gif, bmp, tiff, etc.
    * *pytesseract* [link](https://pypi.org/project/pytesseract/): Python-tesseract is a wrapper for Google’s Tesseract-OCR Engine and uses pillow to provide additional support for most image formats (Tesseract supports only tiff and bmp by default).
    ```
    sudo pip3 install opencv-contrib-python
    sudo pip3 install imutils
    sudo pip3 install pillow
    sudo pip3 install pytesseract
    ```

4. Install Tesseract 4 [link](https://github.com/tesseract-ocr/tesseract/wiki)

    Tesseract is available directly from many Linux distributions. The package is generally called 'tesseract' or 'tesseract-ocr' - search your distribution's repositories to find it. Refer to the installation guide above.

    For Ubuntu:
    ```
    sudo apt install tesseract-ocr
    ```

    For MacOS:
    ```
    brew install tesseract
    ```

    Run "tesseract -v" on the command line to test if tesseract is installed properly:
    ```
    MacBook-Pro:comp90019 malcolmchu$ tesseract -v
    tesseract 4.1.0-rc1
     leptonica-1.78.0
      libgif 5.1.4 : libjpeg 9c : libpng 1.6.37 : libtiff 4.0.10 : zlib 1.2.11 : libwebp 1.0.2 : libopenjp2 2.3.1
     Found AVX2
     Found AVX
     Found SSE
    ```
    
    If you see `tesseract 4` in the output, it means that tesseract has been installed.

## Build

Using standard Maven commands, build the project from the project home directory. Java dependencies are declared in `pom.xml` and may take awhile to download (for the first time). If the build is successful, the JAR file will be available under the `target` directory.

Configure ES settings in `Constants.java` depending on the ES version installed:

**Note**: Mapping types have been removed in ES 7.0 [(click here for details)](https://www.elastic.co/guide/en/elasticsearch/reference/current/removal-of-types.html#_why_are_mapping_types_being_removed):
```java
public static final String ES_SERVER = "localhost";
public static final String ES_PORT = "9200";
// ES 6.7 - index:twitter, type:tweet
// public static final String ES_INDEX = "twitter/tweet";
// ES 7.0 - index:twitter-tweet (type has been removed)
public static final String ES_INDEX = "twitter-tweet";
public static final String ES_SUBMIT_BATCH_SIZE = "500";
public static final String ES_SUBMIT_INTERVAL_IN_SECONDS = "15";
```

To clean the build, execute:
```
mvn clean
```

Sample output:
```
[INFO] 
[INFO] -----------------------< edu.unimelb:comp90019 >------------------------
[INFO] Building comp90019 0.0.1-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- maven-clean-plugin:2.5:clean (default-clean) @ comp90019 ---
[INFO] Deleting /Users/malcolmchu/Repository/comp90019/target
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  0.235 s
[INFO] Finished at: 2019-04-22T08:35:59+10:00
[INFO] ------------------------------------------------------------------------
```

To build the project, execute:
```
mvn package
```

Sample output:
```
[INFO] 
[INFO] -----------------------< edu.unimelb:comp90019 >------------------------
[INFO] Building comp90019 0.0.1-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
...
[INFO] Replacing /Users/malcolmchu/Repository/comp90019/target/comp90019-0.0.1-SNAPSHOT.jar with /Users/malcolmchu/Repository/comp90019/target/comp90019-0.0.1-SNAPSHOT-shaded.jar
[INFO] Dependency-reduced POM written at: /Users/malcolmchu/Repository/comp90019/dependency-reduced-pom.xml
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  27.681 s
[INFO] Finished at: 2019-04-22T08:37:09+10:00
[INFO] ------------------------------------------------------------------------
```

## Deployment

Either start the storm cluster **1 a)** manually or **1 b)** under supervision *(recommended)*

**1 a)** Start the storm cluster manually.

* Start zookeeper from its installation directory e.g. /Developer/zookeeper-3.4.13/bin/
  ``` 
  ./zkServer.sh start
  ```

* Start storm from its installation directory e.g. /Developer/apache-storm-1.2.2/bin/
  ```
  storm nimbus
  storm supervisor
  storm ui
  ```

**Note**: Storm UI can be accessed via <http://localhost:8080>.

**1 b)** Start the storm cluster under supervision *(recommended)*.

The following processes are fail-fast (will halt when an error is encountered) and must be restarted automatically by a supervisory process.
* Zookeeper using daemontools or SMF [link](http://zookeeper.apache.org/doc/r3.3.3/zookeeperAdmin.html#sc_supervision).
* Nimbus and Supervisor using daemontools or monit [link](http://storm.apache.org/releases/current/Fault-tolerance.html)

You can choose any supervisory process to monitor Zookeeper, Nimbus and Supervisor. We have chosen supervisord and the setup instructions is available [here](https://morgankenyon.wordpress.com/2015/08/22/running-apache-storm-under-supervision-supervisord/).

* Install supervisor using pip
  ```
  sudo pip install supervisor
  ```

* Make a copy of the default supervisord conf
  ```
  echo_supervisord_conf > /Developer/supervisor/supervisord.conf
  ```

* Modify the new supervisord.conf by adding the commands to start zookeeper, nimbus, supervisor and ui
  ```
  ; run zookeeper, nimbus, supervisor, ui under supervision
  [program:zookeeper]
  command=/Developer/zookeeper-3.4.13/bin/zkServer.sh start-foreground

  [program:storm_nimbus]
  command=/Developer/apache-storm-1.2.2/bin/storm nimbus

  [program:storm_supervisor]
  command=/Developer/apache-storm-1.2.2/bin/storm supervisor

  [program:storm_ui]
  command=/Developer/apache-storm-1.2.2/bin/storm ui
  ```

* Start storm cluster under supervision by executing the following:
  ```
  supervisord -c /Developer/supervisor/supervisord.conf
  supervisorctl -c /Developer/supervisor/supervisord.conf
  ```

**2.** Start Elasticsearch from its installation directory
```
./bin/elasticsearch
```

**3.** Start Kibana from its installation directory
```
./bin/kibana
```

**4.** Create the twitter index on Elasticsearch

Launch Elasticsearch UI and Kibana UI on the browser

  * Elasticsearch Indices <http://localhost:9200/_cat/indices?v>
  * Kibana Dashboard <http://localhost:5601/app/kibana>

**Important**: Elasticsearch indices must be set up in Kibana before running the storm topology.

Deleting an index (including all its data):
1. Go to Kibana > Dev Tools
2. Type command `DELETE <index-name>` into the console
3. Press play (next to command)

Create the twitter index:
1. Go to Kibana > Dev Tools
2. Copy the contents from `elastic/es7_index_mapping.json` into the console
3. Press play (next to command)

Below is a sample of the twitter index structure.
```
PUT twitter-tweet
{
  "settings":{
    "number_of_shards":5,
    "number_of_replicas":1,
    "analysis":{
    ...
    }
  },
  "mappings": {
    "properties": {
    ...
    }
  }
}
```

**5.** Import saved index-patterns, visualisation and dashboard in Kibana

1. Go to Kibana > Management > Kibana (Saved Objects)
2. Click on Import (at the top) to bring up the import side window
3. Click on Import (link) and select `kibana/k7_export.json` from the project directory
4. Click on Import (button) at the bottom of the side window

Once successfully imported, you will be able to go to `Discover`, `Visualize` and `Dashboard` in the left side bar of Kibana to open saved objects that have been pre-built for this project.

**6 a)** Run storm topology on a storm cluster
```
storm jar target/comp90019-0.0.1-SNAPSHOT.jar edu.unimelb.comp90019.TwitterTopology -t <topology-id>
```

Supported flags
* -t "storm topology id e.g. twitter-trends; if unspecified, storm will run topology in local mode"
* -i "sample tweets by place (woeid) trends; default = 1103816 (Melbourne)"
* -k "sample tweets by additional keywords, in quotes \"keyword1 keyword2 ... \" "

**6 b)** Stop storm topology
```
storm kill <topology-id>
```

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
* [Apache Storm](https://storm.apache.org/) - Distributed Realtime Computation System
* [Elasticsearch](https://www.elastic.co/products/elasticsearch) - Distributed, RESTful search and analytics engine
* [Kibana](https://www.elastic.co/products/kibana) - Visualize data in Elasticsearch

## Authors

* **Malcolm Chu** - [malcolmchu](https://github.com/malcolmchu)

See also the list of [contributors](https://github.com/malcolmchu/comp90019/contributors) who participated in this project.
* **ABC** - [abc](https://github.com/abc)
* **XYZ** - [xyz](https://github.com/xyz)

## License

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>

## Acknowledgments

