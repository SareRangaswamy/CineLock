package com.CineLock.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cineLockOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CineLock API")
                        .description("High-Concurrency Movie Ticket Booking System")
                        .version("1.0"));
    }
}