
# Cloud-native, Reactive, live score streaming app development workshop
This repository contains the source code and the resources for workshop from Spring IO 2019, Barcelona.

https://2019.springio.net/sessions/cloud-native-reactive-spring-boot-application-development-workshop

At this final step, you are expected to build gateway service. Gateway service will be responsible for routing the requests coming from clients to either team-service, or live-score-service.

in the scope of this section, two new apps are provided 'team-service' and 'gateway-service'. go ahead and import those modules

## team-service application 

this application has nothing special, but providing a simple Rest service which will return team information from just in memory data.

it has all the required dependencies and configuration data so that just like live-score-service, it will register itself in service-registry. 

go ahead, run the app TeamServiceApplication.java 

verify that it works 

```
curl -X GET http://localhost:8081/team/Fenerbahce
```

should return 

```
{
  "name": "Fenerbahçe",
  "coach": "Ersun Yanal",
  "city": "İstanbul",
  "stadium": "Şükrü Saracoğlu",
  "establishedYear": 1907
}
```

## gateway-service application 

it's very easy to have a gateway-service application. all it takes is following

+ maven dependency
+ @SpringBootApplication
+ @EnableZuulProxy
+ configuration data

### maven dependencies
add the following dependency in gateway-service/pom.xml 

```
	<dependencies>
	    
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-zuul</artifactId>
		</dependency>

	    ...
	    ...
```

### @EnableZuulProxy

add the required annotation which is missing to main class GatewayServiceApplication.java so that the Zuul proxy will be enabled

```
@EnableZuulProxy
```

```
@SpringBootApplication
public class GatewayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayServiceApplication.class, args);
	}

}
```

### routing configuration

specify routing rules in gateway-service/src/main/resources/application.properties. following configuration declares two different routes

+ route to team-service
+ route to live-score-service

pay attention to serviceId's, which should be identical to service names (spring.application.name)

```
zuul.routes.team.path=/team-service
zuul.routes.team.serviceId=team-service

zuul.routes.livescore.path=/live-score-service
zuul.routes.livescore.serviceId=live-score-service
```

make sure that all of the other services are running. if not, start them in the following order 
+ config-service
+ service-registry
+ team-service
+ live-score-service

now, it's time to run the gateway service from GatewayServiceApplication.java main class. 


verify that gateway service is running, and it routes the requests properly to dedicated micro service

```
curl -X GET http://localhost:8000/live-score-service/match/1
curl -X GET http://localhost:8000/team-service/team/Fenerbahce
```

you should see expected responses from live-score-service & team-service correspondinly
 


