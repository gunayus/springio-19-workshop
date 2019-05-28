package org.springmeetup.livescoreservice.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springmeetup.livescoreservice.model.Match;
import org.springmeetup.livescoreservice.service.ApiRestService;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class ApiRestController {

	private final ApiRestService apiRestService;

	@GetMapping("/match/{id}")
	public Mono<Match> getMatchById(@PathVariable("id") Long id) {
		return apiRestService.findMatchById(id);
	}
}
