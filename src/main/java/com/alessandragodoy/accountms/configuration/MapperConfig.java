package com.alessandragodoy.accountms.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for creating a {@link ModelMapper} bean.
 * This class provides a default configuration for mapping objects.
 */
@Configuration
public class MapperConfig {

	@Bean
	public ModelMapper defaultMApper() {
		return new ModelMapper();
	}
}
