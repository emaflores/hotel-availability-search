package com.mindata.hotelavailability.infrastructure.adapter.out.kafka.producer;

import com.mindata.hotelavailability.domain.model.HotelSearch;
import com.mindata.hotelavailability.domain.port.out.SearchEventPublisher;
import com.mindata.hotelavailability.infrastructure.adapter.out.kafka.message.HotelSearchMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class SearchKafkaProducer implements SearchEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchKafkaProducer.class);
    private static final long SEND_TIMEOUT_SECONDS = 10L;

    private final KafkaTemplate<String, HotelSearchMessage> kafkaTemplate;
    private final String topic;

    public SearchKafkaProducer(
            KafkaTemplate<String, HotelSearchMessage> kafkaTemplate,
            @Value("${app.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(HotelSearch search) {
        HotelSearchMessage message = HotelSearchMessage.from(search);
        try {
            kafkaTemplate.send(topic, search.searchId(), message)
                    .get(SEND_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            LOGGER.debug("Published search {} to topic {}", search.searchId(), topic);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new KafkaException("Interrupted while publishing search " + search.searchId(), ex);
        } catch (ExecutionException | TimeoutException ex) {
            throw new KafkaException("Failed to publish search " + search.searchId(), ex);
        }
    }
}
