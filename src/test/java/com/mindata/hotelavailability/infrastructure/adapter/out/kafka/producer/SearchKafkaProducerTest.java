package com.mindata.hotelavailability.infrastructure.adapter.out.kafka.producer;

import com.mindata.hotelavailability.domain.model.HotelSearch;
import com.mindata.hotelavailability.infrastructure.adapter.out.kafka.message.HotelSearchMessage;
import com.mindata.hotelavailability.infrastructure.adapter.out.kafka.producer.SearchKafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SearchKafkaProducerTest {

    @Test
    @SuppressWarnings("unchecked")
    void publishSendsToConfiguredTopicAndWaitsForAck() {
        KafkaTemplate<String, HotelSearchMessage> template = mock(KafkaTemplate.class);
        SendResult<String, HotelSearchMessage> sendResult = new SendResult<>(
                new ProducerRecord<>("test-topic", "id-1", null),
                new RecordMetadata(new TopicPartition("test-topic", 0), 0L, 0, 0L, 0, 0));
        when(template.send(eq("test-topic"), eq("id-1"), any(HotelSearchMessage.class)))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        SearchKafkaProducer producer = new SearchKafkaProducer(template, "test-topic");
        HotelSearch search = new HotelSearch(
                "id-1", "h", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2), List.of(1, 2));

        producer.publish(search);

        ArgumentCaptor<HotelSearchMessage> value = ArgumentCaptor.forClass(HotelSearchMessage.class);
        verify(template).send(eq("test-topic"), eq("id-1"), value.capture());
        assertAll(
                () -> assertEquals("id-1", value.getValue().searchId()),
                () -> assertEquals(List.of(1, 2), value.getValue().ages())
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void publishWrapsSendFailureAsKafkaException() {
        KafkaTemplate<String, HotelSearchMessage> template = mock(KafkaTemplate.class);
        CompletableFuture<SendResult<String, HotelSearchMessage>> failed = new CompletableFuture<>();
        failed.completeExceptionally(new RuntimeException("broker down"));
        when(template.send(anyString(), anyString(), any(HotelSearchMessage.class))).thenReturn(failed);

        SearchKafkaProducer producer = new SearchKafkaProducer(template, "t");
        HotelSearch search = new HotelSearch(
                "id-1", "h", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2), List.of(1));

        assertThrows(KafkaException.class, () -> producer.publish(search));
    }
}
