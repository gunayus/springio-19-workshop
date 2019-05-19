package org.springmeetup.livescoreservice.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springmeetup.livescoreservice.service.ApiRestService;

@RestController
@RequiredArgsConstructor
public class ApiRestController {

	private final ApiRestService apiRestService;

}
