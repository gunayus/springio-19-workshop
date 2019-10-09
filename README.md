
# Cloud-native, Reactive, live score streaming app development workshop
This repository contains the source code and the resources for workshop from Spring IO 2019, Barcelona.

https://2019.springio.net/sessions/cloud-native-reactive-spring-boot-application-development-workshop

## config-service application (the config server)

At this step, you are expected to build spring cloud service registry. All of our services will register themselves on service registry so that router service may discover live-score-service instances and route the requests properly.
 

in the scope of this section, a new app service-registry is provided. go ahead and import that module

it's very easy to have a service-registry application. all it takes is following
+ maven dependency
+ @SpringBootApplication
+ @EnableEurekaServer
+ configuration data

### maven dependencies
add the following dependency in service-registry/pom.xml 

```
	<dependencies>
	    
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
		</dependency>

	    ...
	    ...
```

### @EnableEurekaServer

add the required annotation which is missing to main class ServiceRegistryApplication.java so that the Eureka server will be enabled

```
@EnableEurekaServer
```

```
@SpringBootApplication
public class ServiceRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceRegistryApplication.class, args);
	}

}
```

now, it's time to run the service registry from ServiceRegistryApplication.java main class. 

verify that service registry is running, go to your browser and open following page

```
http://localhost:8760/
```

you should see spring Eureka homepage without any application instances
 

## live-score-service application (the service to be registered)
as we have the service registry up and running it's time to register the live-score-service application to service registry so that the instances of our service can be discovered by other micro services

add the following dependecy in live-score-service/pom.xml

```
	<dependencies>
	    
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>

	    ...
	    ...
```

specify service registry location in live-score-service/src/main/resources/application.properties

```
eureka.client.service-url.defaultZone=http://localhost:8760/eureka/
```


run live-score-service application LiveScoreServiceApplication.java

go to spring Eureka dashboard again, 

```
http://localhost:8760/
```

you should see one instance of LIVE-SCORE-SERVICE in dashboard


In the next section, we will register our application in Netflix Eureka Server
 
## next section is 08_gateway_service operations

checkout 08_gateway_service branch and follow the instructions in README.md

