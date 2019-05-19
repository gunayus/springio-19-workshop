
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

Let's have a look at what each dependancy does;

+ spring-boot-starter-web : Servlet based Spring Web MVC application 
+ spring-boot-starter-actuator : provides application insights (health status, env, info, metrics, etc.) at runtime
+ spring-boot-devtools : used to redeploy application during development as soon as something changes on classpath (source code compilation or properties changes)
+ lombok : used for auto generating source code for getters / setters / constructors / builders etc. If you have not used before, you will need to install lombok plugin in your IDE (STS, Eclipse, Intellij IDEA, etc.) 

Run LiveScoreServiceApplication.java either as Java Class or by maven

```
cd live-score-service
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

change

```
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
```

to

```
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-webflux</artifactId>
		</dependency>
```

run the LiveScoreApplication.java again, notice that this time the application is run in Netty, rather than Tomcat anymore. 

```
2019-05-19 14:57:21.543  INFO 3200 --- [  restartedMain] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port(s): 8080
```

the second thing is to change the REST endpoint method return type from 'List<Greeting>' to 'Flux<Greeeting>'. please do so;

```
	@GetMapping(value = "/greetings", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Flux<Greeting> greetings() {
		List<Greeting> greetingList = new ArrayList<>();

		return Flux.fromIterable(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
				.stream()
				.map(tick -> Greeting.builder().message("Hello world - " + tick).build())
				.collect(Collectors.toList())
		);
	}
```

when you call this service again, you should see the same response of JSON data containing 10 greeting messages. 

### generate greeting messages at every 1 second

let's now generate these 10 greeting messages with an interval of 1000 ms(1 sec) instead of statically generating the list. 

try to do so yourself, you'll need to use Flux.interval() and .map() methods. If you need assistance, here is the implementation for that.

```
	@GetMapping(value = "/greetings", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Flux<Greeting> greetings() {
		return Flux.interval(Duration.ofMillis(1000))
				.log()
				.map(tick -> Greeting.builder().message("Hello world - " + tick).build())
				.log()
				.take(10);
	}
```

+ Flux.interval(Duration.ofMillis(1000)) : this line produces a Flux (stream) of long ticker values (0, 1, 2, ...) every second
+ .log() : this line logs the data (consequent long values) when each value is generated
+ .map(tick -> Greeting.builder().message("Hello world - " + tick).build()) : this line converts the received long ticker value to a corresponding Greeting object 
+ .log() : the scond log() method prints the Greeting objects in the log becuase we have Greeting objects now in the stream rather than long values
+ .take(10) : this line will make sure that after receiving 10 items the stream will be completed (successfully) and the result will be returned to the client

now if you make the REST request again,  

```
curl -X GET http://localhost:8080/greetings
```

you will see the very same output but with a major difference, you will have to wait 10 seconds before you see the output.

this is a very big problem righ? the whole idea behind going to reactive stack was to eliminate the waitings. but we have to wait until the last piece of data is produced.

### remove waiting 10 seconds, return the data to client as soon as it's produced

to fix this problem, we have to change the response content-type from 'application/json' to 'text/event-stream'

```
	@GetMapping(value = "/greetings", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Greeting> greetings() {
		return Flux.interval(Duration.ofMillis(1000))
				.log()
				.map(tick -> Greeting.builder().message("Hello world - " + tick).build())
				.log()
				.take(10);
	}
```
now we will see that as soon as an item is produced it will be sent to the client wihout waiting until the last item is produced.

```
curl -X GET http://localhost:8080/greetings -i
```

```
HTTP/1.1 200 OK
transfer-encoding: chunked
Content-Type: text/event-stream;charset=UTF-8

data:{"message":"Hello world - 0"}
data:{"message":"Hello world - 1"}
data:{"message":"Hello world - 2"}
data:{"message":"Hello world - 3"}
data:{"message":"Hello world - 4"}
data:{"message":"Hello world - 5"}
data:{"message":"Hello world - 6"}
data:{"message":"Hello world - 7"}
data:{"message":"Hello world - 8"}
data:{"message":"Hello world - 9"}
```

### which thread is running your code?
in non-reactive application development, when a request is received, one available thread (if there is any) from a thread pool is assigned and that thread takes the responsibility to run your code until the response is returned to the client. 

however, in reactive application development model, the whole idea is not to keep these threads bound to the request when there is any kind of I/O operation, leave the thread so that it can server other users, requests, and as soon as the I/O operation returns with data, an other available thread takes the responsibility and resumes the execution of your code. 

let's play with it. if you execute the previous request couple of times, you should see some thing like this in the logs

```
2019-05-19 15:35:56.476  INFO 3814 --- [     parallel-1] reactor.Flux.OnAssembly.1                : | onNext(5)
2019-05-19 15:35:56.476  INFO 3814 --- [     parallel-1] reactor.Flux.OnAssembly.2                : | onNext(Greeting(message=Hello world - 5))
2019-05-19 15:35:57.474  INFO 3814 --- [     parallel-1] reactor.Flux.OnAssembly.1                : | onNext(6)
2019-05-19 15:35:57.474  INFO 3814 --- [     parallel-1] reactor.Flux.OnAssembly.2                : | onNext(Greeting(message=Hello world - 6))
2019-05-19 15:36:11.143  INFO 3814 --- [     parallel-2] reactor.Flux.OnAssembly.3                : | onNext(1)
2019-05-19 15:36:11.143  INFO 3814 --- [     parallel-2] reactor.Flux.OnAssembly.4                : | onNext(Greeting(message=Hello world - 1))
2019-05-19 15:36:12.143  INFO 3814 --- [     parallel-2] reactor.Flux.OnAssembly.3                : | onNext(2)
2019-05-19 15:36:12.144  INFO 3814 --- [     parallel-2] reactor.Flux.OnAssembly.4                : | onNext(Greeting(message=Hello world - 2))
2019-05-19 15:36:16.233  INFO 3814 --- [     parallel-3] reactor.Flux.OnAssembly.5                : | onNext(0)
2019-05-19 15:36:16.234  INFO 3814 --- [     parallel-3] reactor.Flux.OnAssembly.6                : | onNext(Greeting(message=Hello world - 0))

```

this indicates that some of the requests are processed by threads 'parallel-1', 'parallel-2', 'parallel-3', etc. this is beacuse by default the reactive operations are executed by a thread pool of parallel threads which are created according to the number of CPU-cores on your machine. 

if you want to change this behaviour you have to specify by which thread(s) your application is to be executed. 

```
	@GetMapping(value = "/greetings", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Greeting> greetings() {
		return Flux.interval(Duration.ofMillis(1000))
				.publishOn(Schedulers.single())
				.log()
				.map(tick -> Greeting.builder().message("Hello world - " + tick).build())
				.log()
				.take(10);
	}
```
 
pay attention to .publishOn(Schedulers.single()) method. this is how you manage which thread takes the responsibility. there are couple of options

+ Schedulers.single() : all the operations are executed by a single thread no matter what the load is 
+ Schedulers.parallel() : by a pool of threads created according to the number of cores on the system
+ Schedulers.elastic() : this will not limit the number fo threads and extend the thread pool capacity, looks better but can be killing performance with the overhead of thread management, beacuse it's not controlled area. 
+ Schedulers.fromExecutor(someExecutor) : a custom thread pool task executor

try these options and see the results in the log lines. 

## next section is contract driven REST service development 

checkout 03_cloud_contract branch and follow the instructions in README.md

