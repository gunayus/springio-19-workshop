
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

### Redis  


In the next section, we will fetch events from Kafka in a reactive manner
 
## next section is 05_reactive_kafka operations

checkout 05_reactive_kafka branch and follow the instructions in README.md

