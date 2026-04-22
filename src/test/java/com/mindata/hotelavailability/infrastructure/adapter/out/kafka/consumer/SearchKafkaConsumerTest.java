package com.mindata.hotelavailability.infrastructure.adapter.out.kafka.consumer;

import com.mindata.hotelavailability.domain.model.HotelSearch;
import com.mindata.hotelavailability.domain.port.out.SearchRepository;
import com.mindata.hotelavailability.infrastructure.adapter.out.kafka.consumer.SearchKafkaConsumer;
import com.mindata.hotelavailability.infrastructure.adapter.out.kafka.message.HotelSearchMessage;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class SearchKafkaConsumerTest {

    @Test
    void savesIncomingMessage() {
        AtomicReference<HotelSearch> saved = new AtomicReference<>();
        SearchRepository repo = new SearchRepository() {
            @Override public void save(HotelSearch s) { saved.set(s); }
            @Override public Optional<HotelSearch> findById(String id) { return Optional.empty(); }
            @Override public long countEqual(HotelSearch s) { return 0; }
        };

        SearchKafkaConsumer consumer = new SearchKafkaConsumer(repo);
        HotelSearchMessage msg = new HotelSearchMessage(
                "id-1", "h", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2), List.of(1));

        consumer.onMessage(msg);

        assertAll(
                () -> assertNotNull(saved.get()),
                () -> assertEquals("id-1", saved.get().searchId())
        );
    }

    @Test
    void propagatesExceptionSoErrorHandlerCanRetry() {
        SearchRepository repo = new SearchRepository() {
            @Override public void save(HotelSearch s) { throw new IllegalStateException("db down"); }
            @Override public Optional<HotelSearch> findById(String id) { return Optional.empty(); }
            @Override public long countEqual(HotelSearch s) { return 0; }
        };
        SearchKafkaConsumer consumer = new SearchKafkaConsumer(repo);
        HotelSearchMessage msg = new HotelSearchMessage(
                "id", "h", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2), List.of(1));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> consumer.onMessage(msg));
        assertEquals("db down", ex.getMessage());
    }
}
