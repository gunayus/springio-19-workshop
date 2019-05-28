
# Cloud-native, Reactive, live score streaming app development workshop
This repository contains the source code and the resources for workshop from Spring IO 2019, Barcelona.

https://2019.springio.net/sessions/cloud-native-reactive-spring-boot-application-development-workshop

## 04_reactive_redis step

At this step, you are expected to build reactive Redis integration. the repository contains live-score-service module which has teamples of contract files and classes

### maven dependencies

in order to fetch data from Redis in a reactive manner, following dependency is needed

```
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-redis-reactive</artifactId>
		</dependency>
```

### Redis configuration 

once we have the maven dependency we have all the required classes. we need Redis configuration beans for following

+ ReactiveRedisConnectionFactory bean
+ ReactiveRedisTemplate bean

in the base project, RedisConfig.java is provided. add the following Bean method so that it can be used to perform Redis operations

```
	@Bean
	public ReactiveRedisTemplate<String, Match> matchReactiveRedisTemplate(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
		RedisSerializationContext<String, Match> serializationContext = RedisSerializationContext
				.<String, Match>newSerializationContext(new StringRedisSerializer())
				.hashKey(new StringRedisSerializer())
				.hashValue(configureJackson2JsonRedisSerializer(Match.class))
				.build();

		return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, serializationContext);
	}
```

for creating a ReactiveRedisTemplate bean, you need a connection factory and a serialization context object. Seralization context is created with a hashKey and hashValue seralizer

### fetching data from Redis

in order to fetch data from Redis, we will use the ReactiveRedisTemplate bean that we have added in previous step. 

inject the ReactiveRedisTemplate<String, Match> bean in ApiRestService bean

```
@Service
@RequiredArgsConstructor
public class ApiRestService {

	private final ReactiveRedisTemplate<String, Match> matchReactiveRedisTemplate;

	private ReactiveHashOperations<String, String, Match> matchReactiveHashOperations() {
		return matchReactiveRedisTemplate.<String, Match>opsForHash();
	}
```
  
the private method matchReactiveHashOperations() is used to obtain ReactiveHashOperations<String, String, Match> object which will be used for all the Hash opeartions with Redis server. 

implement the findMatchById method in ApiRestService.java


```
	public Mono<Match> findMatchById(Long id) {
		return matchReactiveHashOperations().get("matches", id.toString());
	}
```

matchReactiveHashOperations().get() method arguments

+ "matches" : hash name (sort of a hash map)
+ id.toString() : hash Key 

now we can run LiveScoreServiceApplication.java main class to start the application. perform a GET request for any match id

```
curl -X GET http://localhost:8080/match/1 
```

all of the above requests should return HTTP-200 but with no content because currently there is no match data with the provided id's in Redis yet

### saving data in Redis

let's assume that we will POST match data through our ApiRestController.java delegating the request to ApiRestService.java bean

```
	@PostMapping("/match")
	public Mono<String> saveMatchDetails(@RequestBody Match match) {
		return apiRestService.saveMatchDetails(match);
	}
```


ReactiveHashOperations<String, String, Match> object is used also for saving data in Redis (put operation). add following method in ApiRestService.java class

```
	public Mono<String> saveMatchDetails(Match match) {
		return matchReactiveHashOperations().put("matches", match.getMatchId().toString(), match)
				.map(hashOperationResult -> hashOperationResult ? "OK" : "NOK")
				.onErrorResume(throwable -> Mono.just("EXCEPTION : " + throwable.getMessage()))
				;
	}
```

now let's post a Match details 

```
curl -X POST \
  http://localhost:8080/match \
  -H 'Content-Type: application/json' \
  -d '{
	"match-id": 1,
	"name": "Barcelona - Getafe",
	"start-date": "2019-05-01T19:00:00",
	"status": "COMPLETED",
	"score": "1 - 2",
	"events": [
		{
			"minute": 1, 
			"type": "GOAL",
			"team": "Barcelona",
			"player-name": "Lionel Messi"
		},
		{
			"minute": 45, 
			"type": "RED",
			"team": "Real Madrid",						
			"player-name": "Sergio Ramos"
		},
		{
			"minute": 75, 
			"type": "GOAL",
			"team": "Real Madrid",						
			"player-name": "Luka Modric"
		},
		{
			"minute": 78, 
			"type": "YELLOW",
			"team": "Real Madrid",						
			"player-name": "Luka Modric"
		}
	]
}'
```

and now query again; 

```
curl -X GET http://localhost:8080/match/1 
```

this time you should receive the Math data posted in previous step

In the next section, we will fetch events from Kafka in a reactive manner
 
## next section is 05_reactive_kafka operations

checkout 05_reactive_kafka branch and follow the instructions in README.md

