
# Cloud-native, Reactive, live score streaming app development workshop
This repository contains the source code and the resources for workshop from Spring IO 2019, Barcelona.

https://2019.springio.net/sessions/cloud-native-reactive-spring-boot-application-development-workshop

## 01-initial step

At this step installation of pre-requisites are expected. Please follow the Prerequisites.

## Prerequisites
In order to follow the workshop, it's good idea to have the following prerequisites ready on your system
    

+ JDK 8 or above
+ IDE supporting Spring development, e.g. STS, Eclipse, Intellij IDEA, etc.
+ lombok
+ Redis
+ Kafka

### Installing Lombok
Lombok can be installed as a plug-in to your favourite IDE. Follow the instructions for your IDE
TODO - add lombok link here


### Installing Redis
Redis can be installed in two ways

1 - Build from source code

https://redis.io/topics/quickstart

As explained in Redis quick start installation section, Redis can be built from source code and installed. 

```
wget http://download.redis.io/redis-stable.tar.gz 
tar xvzf redis-stable.tar.gz
cd redis-stable
make
```

2 - Use docker image

Follow instructions listed in Redis official dockerhub page

https://hub.docker.com/_/redis

or you can use following docker-compose.yml content

```
version: '2'

services:
  redis:
    image: 'bitnami/redis:latest'
    environment:
      # ALLOW_EMPTY_PASSWORD is recommended only for development.
      - ALLOW_EMPTY_PASSWORD=yes
    labels:
      kompose.service.type: nodeport
    ports:
      - '6379:6379'
    volumes:
      - ~/volumes/redis:/var/lib/redis
    command: redis-server --requirepass password
```


### Installing Kafka
Download latest kafka release as explained in 

https://kafka.apache.org/quickstart

```
> tar -xzf kafka_2.12-2.2.0.tgz
> cd kafka_2.12-2.2.0
> bin/zookeeper-server-start.sh config/zookeeper.properties
> bin/kafka-server-start.sh config/server.properties
```

## next section is reactive Spring development 

checkout 02_reactive_spring branch and follow the instructions in README.md
