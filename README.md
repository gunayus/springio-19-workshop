
# Cloud-native, Reactive, live score streaming app development workshop
This repository contains the source code and the resources for workshop from Spring IO 2019, Barcelona.

https://2019.springio.net/sessions/cloud-native-reactive-spring-boot-application-development-workshop

## config-service application (the config server)

At this step, you are expected to build spring cloud config service. The live-score-service app receives configuration data from config-service app 

so far we have only worked on live-score-service app. in the scope of this section, a new app config-service is provided. go ahead and import that module

it's very easy to have a config-service application. all it takes is following
+ maven dependency
+ @SpringBootApplication
+ @EnableConfigServer
+ config repository git uri

### maven dependencies
add the following dependency in pom.xml 

```
	<dependencies>
	    
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-config-server</artifactId>
		</dependency>

	    ...
	    ...
```

### @EnableConfigServer

add the required annotation which is missing to main class ConfigServiceApplication.java so that the config server will be enabled

```
@EnableConfigServer
```

```
@SpringBootApplication
public class ConfigServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigServiceApplication.class, args);
	}

}
```

### specify git repository
spring cloud config server can serve config data from a local git repository or from a remote git server.

specify the git-uri in config-service application.properties file (located under config-service/src/main/resources/application.properties)

by default the first one is active and refers to my github repository, and the second one refers to an ordinary local git repo directory. make sure that one of them is active. feel free to change with your own settings if any

```
spring.cloud.config.server.git.uri=https://github.com/gunayus/springio-19-config.git
#spring.cloud.config.server.git.uri=/Users/egunay/workspaces-springio/config-repo
```

now, it's time to run the config server from ConfigServiceApplication.java main class. 

test the config server 

```
curl -X GET http://localhost:8888/live-score-service/default
```

you should get the following response 
```
{
  "name": "live-score-service",
  "profiles": [
    "default"
  ],
  "label": null,
  "version": "2190b18e74cb1093bc37dc3a7494218d7759689c",
  "state": null,
  "propertySources": [
    {
      "name": "https://github.com/gunayus/springio-19-config.git/live-score-service.properties",
      "source": {
        "spring.redis.hostname": "localhost",
        "spring.redis.port": "6379",
        "spring.redis.password": "password",
        "kafka.bootstrap.servers": "localhost:9092",
        "kafka.livescore.topic": "live-score-topic"
      }
    }
  ]
}
```


## live-score-service application (the config client - consumer)
as we have the config server up and running it's time to bind the live-score-service application 

In the next section, we will register our application in Netflix Eureka Server
 
## next section is 07_cloud_service_registry operations

checkout 07_cloud_service_registry branch and follow the instructions in README.md

