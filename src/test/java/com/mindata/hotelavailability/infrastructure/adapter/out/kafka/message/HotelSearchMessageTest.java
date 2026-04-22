package com.mindata.hotelavailability.infrastructure.adapter.out.kafka.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.mindata.hotelavailability.domain.model.HotelSearch;
import com.mindata.hotelavailability.infrastructure.adapter.out.kafka.message.HotelSearchMessage;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HotelSearchMessageTest {

    @Test
    void fromAndToDomainRoundTrip() {
        HotelSearch original = new HotelSearch(
                "id-1", "h", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2), List.of(1, 2, 3));
        HotelSearchMessage message = HotelSearchMessage.from(original);
        HotelSearch back = message.toDomain();

        assertAll(
                () -> assertEquals(original.searchId(), back.searchId()),
                () -> assertEquals(original.hotelId(), back.hotelId()),
                () -> assertEquals(original.checkIn(), back.checkIn()),
                () -> assertEquals(original.checkOut(), back.checkOut()),
                () -> assertEquals(original.ages(), back.ages())
        );
    }

    @Test
    void defensivelyCopiesAgesAndHandlesNull() {
        List<Integer> mutable = new ArrayList<>(Arrays.asList(1, 2));
        HotelSearchMessage copied = new HotelSearchMessage(
                "id", "h", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2), mutable);
        mutable.add(99);

        HotelSearchMessage nullAges = new HotelSearchMessage(
                "id", "h", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2), null);

        assertAll(
                () -> assertEquals(List.of(1, 2), copied.ages()),
                () -> assertNotSame(mutable, copied.ages()),
                () -> assertTrue(nullAges.ages().isEmpty()),
                () -> assertThrows(UnsupportedOperationException.class, () -> copied.ages().add(7))
        );
    }

    @Test
    void jsonSerializationUsesExpectedDateFormat() throws JsonProcessingException {
        ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        HotelSearchMessage msg = new HotelSearchMessage(
                "id-1", "h", LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31), List.of(1));
        String json = mapper.writeValueAsString(msg);
        HotelSearchMessage back = mapper.readValue(json, HotelSearchMessage.class);

        assertAll(
                () -> assertTrue(json.contains("29/12/2023")),
                () -> assertEquals(msg, back)
        );
    }
}
