
# Cloud-native, Reactive, live score streaming app development workshop
This repository contains the source code and the resources for workshop from Spring IO 2019, Barcelona.

https://2019.springio.net/sessions/cloud-native-reactive-spring-boot-application-development-workshop

## 04_reactive_redis step

At this step, you are expected to build reactive Redis integration. the repository contains live-score-service module which has teamples of contract files and classes

### maven dependencies

in order to fetch data from Kafka in a reactive manner, following dependencies are needed

```
		<dependency>
			<groupId>org.springframework.kafka</groupId>
			<artifactId>spring-kafka</artifactId>
		</dependency>
		<dependency>
			<groupId>io.projectreactor.kafka</groupId>
			<artifactId>reactor-kafka</artifactId>
		</dependency>
```

### Kafka configuration 

we need to create two Spring beans for accessing data from Kafka
 
+ KafkaReceiver
+ KafkaSender

in the base project, KafkaConfig.java is provided. add the following Bean methods so that it can be used to perform Kafka operations

```
	@Bean
	KafkaReceiver kafkaReceiver() {

		Map<String, Object> configProps = new HashMap<>();
		configProps.put( ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configProps.put( ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		configProps.put( ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		configProps.put( ConsumerConfig.CLIENT_ID_CONFIG, "live-score-client");
		configProps.put( ConsumerConfig.GROUP_ID_CONFIG, "live-score-group-id");
		configProps.put( ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
 
		return new DefaultKafkaReceiver(ConsumerFactory.INSTANCE,
				ReceiverOptions.create(configProps).subscription(Arrays.asList(topicName))
		);
	}
```

```
	@Bean
	KafkaSender<String, String> kafkaSender() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put( ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configProps.put( ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put( ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.RETRIES_CONFIG, 10);
		configProps.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, "5000");
		configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, "163850"); // 163KByte
		configProps.put(ProducerConfig.LINGER_MS_CONFIG, "100");
		configProps.put(ProducerConfig.ACKS_CONFIG, "1");
		configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "1");

		return new DefaultKafkaSender<>(ProducerFactory.INSTANCE,
				SenderOptions.create(configProps));
	}
```

### sending match events to Kafka

in order to send match events to Kafka, we will update ApiRestService.saveMatchDetails method that posts data to Redis and then to Kafka as well

inject KafkaSender bean in ApiRestService.java

```
	private final KafkaSender<String, String> kafkaSender;
```


```
	public Mono<String> saveMatchDetails(Match match) {
		final String matchStr;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategy.KebabCaseStrategy());

			matchStr = objectMapper.writeValueAsString(match);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		final SenderRecord<String, String, Long> senderRecord =
				SenderRecord.create(new ProducerRecord<String, String>("live-score-topic", matchStr), match.getMatchId());



		return matchReactiveHashOperations().put("matches", match.getMatchId().toString(), match)
				.then(
						kafkaSender.send(Mono.just(senderRecord))
								.next()
								.doOnNext(longSenderResult -> System.out.println(longSenderResult.recordMetadata()))
								.map(longSenderResult -> true)
				)
				.map(hashOperationResult -> hashOperationResult ? "OK" : "NOK")
				.onErrorResume(throwable -> Mono.just("EXCEPTION : " + throwable.getMessage()))
				;
	}
```
  
now we have started posting data to Kafka as a new match event is posted to our application


### consuming match events from Kafka

in order to fetch events from Kafka we will use KafkaReceiver bean that we have configured in previous step 

this calls is provided in the project. KafkaService bean basically receives the events from Kafka as consumerRecords and converts them to ServerSentEvent<String> objects. 

```
package org.springmeetup.livescoreservice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ConnectableFlux;
import reactor.kafka.receiver.KafkaReceiver;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class KafkaService {

	private final KafkaReceiver<String,String> kafkaReceiver;

	private ConnectableFlux<ServerSentEvent<String>> eventPublisher;

	@PostConstruct
	public void init() {
		eventPublisher = kafkaReceiver.receive()
				.map(consumerRecord -> ServerSentEvent.builder(consumerRecord.value()).build())
				.publish();

		// subscribes to the KafkaReceiver -> starts consumption (without observers attached)
		eventPublisher.connect();
	}

	public ConnectableFlux<ServerSentEvent<String>> getEventPublisher() {
		return eventPublisher;
	}

}
```

this Spring bean will give us a ConnectableFlux<ServerSentEvent<String>> object that we can use for setting up Server Sent Event connection between clients and our application

inject KafkaService bean in ApiRestController.java

```
	private final KafkaService kafkaService;
```

add following method in ApiRestController.java which will provide us the Server Sent Event connection from the client

```
	@GetMapping(value = "/match/{id}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<Match>> streamMatchEvents(@PathVariable("id") Long id) {
		return kafkaService.getEventPublisher()
				.log()
				.map(stringServerSentEvent -> {

					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategy.KebabCaseStrategy());

					Match match = null;
					try {
						match = objectMapper.readValue(stringServerSentEvent.data(), Match.class);
					} catch (Exception ex) {
						return null;
					}

					return ServerSentEvent.<Match>builder()
							.data(match)
							.build();
				})
				.log()
				.filter(matchServerSentEvent -> matchServerSentEvent.data().getMatchId().equals(id));
	}
```

run the application again, try to initiate the connection

```
curl -X GET http://localhost:8080/match/20/stream -i
```

this should start the connection and leave it open, now let's post a match event with match id

```
curl -X POST \
  http://localhost:8080/match \
  -H 'Content-Type: application/json' \
  -d '{
	"match-id": 20,
	"name": "Dortmund - Getafe",
	"start-date": "2019-05-29T16:00:00",
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

we should receive this event from the client connection

In the next section, we will configure our application from Spring Cloud Config
 
## next section is 06_cloud_config operations

checkout 06_cloud_config branch and follow the instructions in README.md

