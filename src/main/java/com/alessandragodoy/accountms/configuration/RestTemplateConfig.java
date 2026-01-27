package com.alessandragodoy.accountms.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Configuration class for creating a {@link RestTemplate} bean.
 */
@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

	private final RestTemplateBuilder restTemplateBuilder;

	@Bean
	public RestTemplate restTemplate() {

		return restTemplateBuilder
				.setConnectTimeout(Duration.ofSeconds(5))
				.setReadTimeout(Duration.ofSeconds(5))
				.build();
	}
}
