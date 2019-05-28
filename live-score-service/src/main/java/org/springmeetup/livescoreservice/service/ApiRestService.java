package org.springmeetup.livescoreservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springmeetup.livescoreservice.model.Match;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ApiRestService {

	public Mono<Match> findMatchById(@PathVariable("id") Long id) {
		return null;
	}

}
