package com.mindata.hotelavailability.infrastructure.adapter.out.kafka.consumer;

import com.mindata.hotelavailability.domain.port.out.SearchRepository;
import com.mindata.hotelavailability.infrastructure.adapter.out.kafka.message.HotelSearchMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SearchKafkaConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchKafkaConsumer.class);

    private final SearchRepository searchRepository;

    public SearchKafkaConsumer(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "${app.kafka.group-id}")
    public void onMessage(HotelSearchMessage message) {
        LOGGER.debug("Consumed search {}", message.searchId());
        searchRepository.save(message.toDomain());
    }
}
