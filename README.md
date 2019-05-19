
# Cloud-native, Reactive, live score streaming app development workshop
This repository contains the source code and the resources for workshop from Spring IO 2019, Barcelona.

https://2019.springio.net/sessions/cloud-native-reactive-spring-boot-application-development-workshop

## 02-reactive_spring step

At this step, you are expected to convert a regular servlet based REST controller to a reactive REST controller. Please follow the instructions.

### non-reactive REST controller (List<Greeting>)
In the project, you will see the module 'live-score-service' which is created for you and contains a sample ApiRestController.java class for returning 10 greeting messages in a list. 
please pay attention to pom.xml and see that it's derived from the parent 'spring-boot-starter-parent' and contains the dependency 'spring-boot-starter-web'


```
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.1.5.RELEASE</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>

	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<scope>runtime</scope>
		</dependency>
		
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
```

Run LiveScoreServiceApplication.java either as Java Class or by maven

```
mvn spring-boot:run
```

You should notice that the application is started in Tomcat. 

```
2019-05-19 14:47:27.384  INFO 2961 --- [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
2019-05-19 14:47:27.469  INFO 2961 --- [  restartedMain] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2019-05-19 14:47:27.469  INFO 2961 --- [  restartedMain] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.19]
2019-05-19 14:47:27.814  INFO 2961 --- [  restartedMain] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2019-05-19 14:47:27.815  INFO 2961 --- [  restartedMain] o.s.web.context.ContextLoader            : Root WebApplicationContext: initialization completed in 2655 ms
2019-05-19 14:47:28.677  INFO 2961 --- [  restartedMain] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'applicationTaskExecutor'
2019-05-19 14:47:29.275  INFO 2961 --- [  restartedMain] o.s.b.d.a.OptionalLiveReloadServer       : LiveReload server is running on port 35729
2019-05-19 14:47:29.296  INFO 2961 --- [  restartedMain] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 2 endpoint(s) beneath base path '/actuator'
2019-05-19 14:47:29.704  INFO 2961 --- [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
```

Call the greetings service
```
curl -X GET http://localhost:8080/greetings
```

You should see 10 simple greeting messages as JSON data. 


### convert to Reactive REST controller (Flux<Greeting>)
Now it's time to convert the given RestController to reactive. 

In order to do so, first thing that needs to be done is to change the spring boot web starter to webflux starter so that we have the reactive stack available.