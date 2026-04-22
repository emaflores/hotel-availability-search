package com.mindata.hotelavailability.application.service;

import com.mindata.hotelavailability.application.service.CreateSearchService;
import com.mindata.hotelavailability.domain.exception.InvalidSearchException;
import com.mindata.hotelavailability.domain.model.HotelSearch;
import com.mindata.hotelavailability.domain.port.in.CreateSearchUseCase.CreateSearchCommand;
import com.mindata.hotelavailability.domain.port.out.SearchEventPublisher;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class CreateSearchServiceTest {

    @Test
    void generatesUuidAndPublishesDomainSearch() {
        AtomicReference<HotelSearch> captured = new AtomicReference<>();
        CreateSearchService service = new CreateSearchService(captured::set);

        String id = service.createSearch(new CreateSearchCommand(
                "hotel", LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31),
                List.of(30, 29, 1, 3)));

        assertAll(
                () -> assertNotNull(captured.get()),
                () -> assertEquals(id, captured.get().searchId()),
                () -> assertEquals(UUID.fromString(id).toString(), id),
                () -> assertEquals("hotel", captured.get().hotelId()),
                () -> assertEquals(List.of(30, 29, 1, 3), captured.get().ages())
        );
    }

    @Test
    void propagatesDomainValidationErrors() {
        SearchEventPublisher publisher = s -> {};
        CreateSearchService service = new CreateSearchService(publisher);
        assertThrows(InvalidSearchException.class, () ->
                service.createSearch(new CreateSearchCommand(
                        "", LocalDate.now(), LocalDate.now().plusDays(1), List.of(1))));
    }
}
