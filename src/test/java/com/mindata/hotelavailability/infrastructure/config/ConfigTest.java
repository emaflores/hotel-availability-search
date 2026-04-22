package com.mindata.hotelavailability.infrastructure.config;

import com.mindata.hotelavailability.application.service.CountSearchService;
import com.mindata.hotelavailability.application.service.CreateSearchService;
import com.mindata.hotelavailability.domain.model.HotelSearch;
import com.mindata.hotelavailability.domain.port.in.CountSearchUseCase;
import com.mindata.hotelavailability.domain.port.in.CreateSearchUseCase;
import com.mindata.hotelavailability.domain.port.out.SearchEventPublisher;
import com.mindata.hotelavailability.domain.port.out.SearchRepository;
import com.mindata.hotelavailability.infrastructure.adapter.out.kafka.message.HotelSearchMessage;
import com.mindata.hotelavailability.infrastructure.config.ApplicationServiceConfig;
import com.mindata.hotelavailability.infrastructure.config.KafkaConfig;
import com.mindata.hotelavailability.infrastructure.config.OpenApiConfig;
import io.swagger.v3.oas.models.OpenAPI;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

    @Test
    void openApiBeanContainsMetadata() {
        OpenAPI api = new OpenApiConfig().hotelAvailabilityOpenApi();
        assertAll(
                () -> assertNotNull(api.getInfo()),
                () -> assertEquals("Hotel Availability Search API", api.getInfo().getTitle()),
                () -> assertEquals("1.0.0", api.getInfo().getVersion())
        );
    }

    @Test
    void applicationServiceConfigWiresUseCases() {
        SearchRepository repo = new SearchRepository() {
            @Override public void save(HotelSearch s) { }
            @Override public Optional<HotelSearch> findById(String id) { return Optional.empty(); }
            @Override public long countEqual(HotelSearch s) { return 0; }
        };
        SearchEventPublisher publisher = s -> { };

        ApplicationServiceConfig config = new ApplicationServiceConfig();
        CreateSearchUseCase create = config.createSearchUseCase(publisher);
        CountSearchUseCase count = config.countSearchUseCase(repo);

        assertAll(
                () -> assertInstanceOf(CreateSearchService.class, create),
                () -> assertInstanceOf(CountSearchService.class, count)
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void kafkaConfigProducesFactoriesTemplateAndTopics() {
        KafkaConfig config = new KafkaConfig("localhost:9092", "hotel_availability_searches", "group");
        NewTopic topic = config.hotelAvailabilitySearchesTopic();
        NewTopic dlt = config.hotelAvailabilitySearchesDlt();
        ProducerFactory<String, HotelSearchMessage> pf = config.producerFactory();
        ConsumerFactory<String, HotelSearchMessage> cf = config.consumerFactory();
        KafkaTemplate<String, HotelSearchMessage> template = config.kafkaTemplate(pf);
        DefaultErrorHandler errorHandler = config.kafkaErrorHandler(template);
        ConcurrentKafkaListenerContainerFactory<String, HotelSearchMessage> lf =
                config.kafkaListenerContainerFactory(cf, errorHandler);

        assertAll(
                () -> assertEquals("hotel_availability_searches", topic.name()),
                () -> assertTrue(dlt.name().endsWith(".DLT")),
                () -> assertNotNull(pf),
                () -> assertNotNull(cf),
                () -> assertNotNull(template),
                () -> assertNotNull(errorHandler),
                () -> assertNotNull(lf)
        );
    }
}