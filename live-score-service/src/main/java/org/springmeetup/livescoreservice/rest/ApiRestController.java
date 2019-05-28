package org.springmeetup.livescoreservice.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springmeetup.livescoreservice.model.Match;
import org.springmeetup.livescoreservice.service.ApiRestService;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ApiRestController {

	private final ApiRestService apiRestService;

	@GetMapping("/match/{id}")
	public Mono<Match> getMatchById(@PathVariable("id") Long id) {
		return apiRestService.findMatchById(id);
	}

	@PostMapping("/match")
	public Mono<String> saveMatchDetails(@RequestBody Match match) {
		return apiRestService.saveMatchDetails(match);
	}

}
