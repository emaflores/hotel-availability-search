package com.mindata.hotelavailability.infrastructure.adapter.out.persistence.entity;

import com.mindata.hotelavailability.infrastructure.adapter.out.persistence.entity.SearchEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchEntityTest {

    @Test
    void accessorsReturnConstructorValues() {
        SearchEntity e = new SearchEntity(
                "id-1", "h", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2), "1,2,3");

        assertAll(
                () -> assertEquals("id-1", e.getSearchId()),
                () -> assertEquals("h", e.getHotelId()),
                () -> assertEquals(LocalDate.of(2024, 1, 1), e.getCheckIn()),
                () -> assertEquals(LocalDate.of(2024, 1, 2), e.getCheckOut()),
                () -> assertEquals("1,2,3", e.getAgesCsv())
        );
    }
}
