package org.springmeetup.livescoreservice;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class GreetingRestController {

	@GetMapping(value = "/greetings", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<Greeting> greetings() {
		List<Greeting> greetingList = new ArrayList<>();

		return Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
				.stream()
				.map(tick -> Greeting.builder().message("Hello world - " + tick).build())
				.collect(Collectors.toList());
	}


}

@Data
@Builder
class Greeting {

	private String message;

}