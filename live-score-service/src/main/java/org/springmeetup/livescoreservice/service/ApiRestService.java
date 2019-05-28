package org.springmeetup.livescoreservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springmeetup.livescoreservice.model.Match;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ApiRestService {

	private final ReactiveRedisTemplate<String, Match> matchReactiveRedisTemplate;

	private ReactiveHashOperations<String, String, Match> matchReactiveHashOperations() {
		return matchReactiveRedisTemplate.<String, Match>opsForHash();
	}

	public Mono<Match> findMatchById(Long id) {
		return matchReactiveHashOperations().get("matches", id.toString());
	}

	public Mono<String> saveMatchDetails(Match match) {
		return matchReactiveHashOperations().put("matches", match.getMatchId().toString(), match)
				.map(hashOperationResult -> hashOperationResult ? "OK" : "NOK")
				.onErrorResume(throwable -> Mono.just("EXCEPTION : " + throwable.getMessage()))
				;
	}
}
