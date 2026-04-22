package com.mindata.hotelavailability.infrastructure.config;

import com.mindata.hotelavailability.application.service.CountSearchService;
import com.mindata.hotelavailability.application.service.CreateSearchService;
import com.mindata.hotelavailability.domain.port.in.CountSearchUseCase;
import com.mindata.hotelavailability.domain.port.in.CreateSearchUseCase;
import com.mindata.hotelavailability.domain.port.out.SearchEventPublisher;
import com.mindata.hotelavailability.domain.port.out.SearchRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationServiceConfig {

    @Bean
    public CreateSearchUseCase createSearchUseCase(SearchEventPublisher eventPublisher) {
        return new CreateSearchService(eventPublisher);
    }

    @Bean
    public CountSearchUseCase countSearchUseCase(SearchRepository searchRepository) {
        return new CountSearchService(searchRepository);
    }
}
