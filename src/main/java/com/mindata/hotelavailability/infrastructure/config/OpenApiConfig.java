package com.mindata.hotelavailability.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI hotelAvailabilityOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Hotel Availability Search API")
                .description("Búsquedas de disponibilidad y conteo hotelero")
                .version("1.0.0"));
    }
}
