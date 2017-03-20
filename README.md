AnotherKafkaMonitor
===========

## ENV
* scala-2.10.*
* kafka-0.10.*
* zookeeper-3.4.6

## RELEASE INFO
* [AnotherKafkaMonitor-v1.1.0.war](https://github.com/triffic-tang/AnotherKafkaMonitor-AKM-/blob/master/AnotherKafkaMonitor-v1.1.0.war)
	- Fix some bugs mentioned in the issues.
* [AnotherKafkaMonitor-v1.2.0.war](https://github.com/triffic-tang/AnotherKafkaMonitor-AKM-/blob/master/AnotherKafkaMonitor-v1.2.0.war)
	- Add SMS Module. Some confiuration steps should be taken before use:
		- Confiure username and password of [webchinese](http://sms.webchinese.cn/reg.shtml) in `system-config.properties`, if you don't have, please register first.
		- 


![Build Status](/WebContent/media/readmepic/build-passing.png)
-------
## Table Of Contents
* [Inspiration](https://github.com/triffic-tang/AnotherKafkaMonitor#inspiration)
* [Kafka DataStructure In Zookeeper](https://github.com/triffic-tang/AnotherKafkaMonitor#kafka-datastructure-in-zookeeper)
* [How To Install](https://github.com/triffic-tang/AnotherKafkaMonitor#how-to-install)
* [Quick Look](https://github.com/triffic-tang/AnotherKafkaMonitor#quick-look)
    * [DashBoard](https://github.com/triffic-tang/AnotherKafkaMonitor#1-dashboard)
    * [Topic List](https://github.com/triffic-tang/AnotherKafkaMonitor#2-topic-list)
    * [Cluster Info](https://github.com/triffic-tang/AnotherKafkaMonitor#3-cluster-info)
    * [Consumers](https://github.com/triffic-tang/AnotherKafkaMonitor#4-consumers)
    * [Alarm Configuration](https://github.com/triffic-tang/AnotherKafkaMonitor#5-alarm-configuration)
    * [Zookeeper Client](https://github.com/triffic-tang/AnotherKafkaMonitor#6-zookeeper-client)
* [Difference Between KafkaOffsetMonitor And AnotherKafkaMonitor](https://github.com/triffic-tang/AnotherKafkaMonitor#difference)
* [Future Plan](https://github.com/triffic-tang/AnotherKafkaMonitor#future-plan)
* [Contributing](https://github.com/triffic-tang/AnotherKafkaMonitor#contributing)

------

Also you can take a quick look before take action(**SOME ADDITIONAL INFO**), ***CLICK***->[Anotherkafkamonitor Manul Book](https://triffic-tang.gitbooks.io/anotherkafkamonitor-manuk-book/content/)

------

## Inspiration
AnotherKafkaMonitor is an app which used to monitor kafka producer and consumer progress inspried by [KafkaOffsetMonitor](https://github.com/quantifind/KafkaOffsetMonitor). It aims to help you figure out what's going on in your kafka cluster, that's to say, to understand how fast the producer send message to the kafka or whether the consumer is far behind the producer or not, if lag exceeds threshold, you will be noticed through alarm email.

## Kafka DataStructure In Zookeeper
The Implementation Of AnotherKafkaMonitor Relys On the Datastructure of Kafka In Zookeeper
![Kafka DataStructure In Zookeeper](/WebContent/media/readmepic/kafka_in_zookeeper.png)

## How To Install
Several ways can be taken if you want to try
* Simple download [AnotherKafkaMonitor.war](https://github.com/triffic-tang/AnotherKafkaMonitor/blob/master/AnotherKafkaMonitor.war) and take a quickstart

> 1. Extract `AnotherKafkaMonitor.war` into default folder called `AnotherKafkaMonitor`;
2. Edit file which located in path of `\AnotherKafkaMonitor\WEB-INF\classes\system-config.properties`, In corresponding with your Zookeeper and Kafka Cluster Environment;
3. Move folder `AnotherKakfaMonitor` to `<TOMCAT_HOME>\webapp`;
4. Start tomcat, type `http://localhost:<TOMCAT_HTTP_PORT>/AnotherKafkaMonitor` into browser, please enjoy.

* Maybe you want to make some change for your taste.

> 1. Download the `zip` file;
2. Import into your IDE, like `Eclipse` or others as you like.

## Quick Look
Now, we are going to show you some screenshot of main featrues about [AnotherKafkaMonitor](https://github.com/triffic-tang/AnotherKafkaMonitor)

### 1. DashBoard
Dashboard lists some general info:
* How many kafka ***Brokers, Topics, Zookeepers*** and ***Consumers*** you hava in cluster
![DashBoard](/WebContent/media/readmepic/akm-dashboard.png)

### 2. Topic List
* List Topics you have created;
* List Partition Indexes of each topic.
![Topic List](/WebContent/media/readmepic/akm-topiclist.png)

### 3. Cluster Info
* Kafka Broker List
* Zookeeper List
![Cluster Info](/WebContent/media/readmepic/akm-clusterinfo.png)

### 4. Consumers
* List how many consumer group you have
* The topic(s) consumed by each consumer group
* RealTime process rate about producer and consumer of one topic
![Consumers](/WebContent/media/readmepic/akm-consumers.png)
![Topic Detail](/WebContent/media/readmepic/akm-topicsdetail.png)
![Topic Realtime](/WebContent/media/readmepic/akm-realtime.png)

### 5. Alarm Configuration
* Alarm notice list
* Add alarm notice
![Alarm List](/WebContent/media/readmepic/akm-alarmadd.png)

### 6. Zookeeper Client
>Support simple shell comand, such as ```ls, get and delete```

![Zookeeper Client](/WebContent/media/readmepic/akm-zkshell.png)


## Difference
We are going to list the difference between [KafkaOffsetMonitor](https://github.com/quantifind/KafkaOffsetMonitor) and [AnotherKafkaMonitor](https://github.com/triffic-tang/AnotherKafkaMonitor)
* AnotherKafkaMonitor is much lightweight compared with KafkaOffsetMonitor;
* AnotherKafkaMonitor is implemented by `Java`, Not `Scala` which KafkaOffsetMonitor be implemented.

## Future Plan
We are going to make some big step in the future, like below(JUST FOR NOW):
* Add `SMS` notice module;
* Support more shell command in the future
* ...

## Contributing
The AnotherKafkaMonitor is released under the Apache License and we welcome any contributions within this license. Any pull request is welcome and will be reviewed and merged as quickly as possible.