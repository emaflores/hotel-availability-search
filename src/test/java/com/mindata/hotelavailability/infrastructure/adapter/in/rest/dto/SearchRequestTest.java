package com.mindata.hotelavailability.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.mindata.hotelavailability.domain.port.in.CreateSearchUseCase.CreateSearchCommand;
import com.mindata.hotelavailability.infrastructure.adapter.in.rest.dto.SearchRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SearchRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validRequestPassesValidationAndMapsToCommand() {
        SearchRequest req = new SearchRequest(
                "h", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2), List.of(30, 29, 1, 3));

        Set<ConstraintViolation<SearchRequest>> violations = validator.validate(req);
        CreateSearchCommand cmd = req.toCommand();

        assertAll(
                () -> assertTrue(violations.isEmpty()),
                () -> assertEquals("h", cmd.hotelId()),
                () -> assertEquals(List.of(30, 29, 1, 3), cmd.ages()),
                () -> assertTrue(req.isDateRangeValid())
        );
    }

    @Test
    void dateRangeValidationTreatsNullAsValid() {
        SearchRequest a = new SearchRequest("h", null, LocalDate.of(2024, 1, 2), List.of(1));
        SearchRequest b = new SearchRequest("h", LocalDate.of(2024, 1, 1), null, List.of(1));

        assertAll(
                () -> assertTrue(a.isDateRangeValid()),
                () -> assertTrue(b.isDateRangeValid())
        );
    }

    @Test
    void dateRangeValidationFailsWhenCheckInEqualsOrAfterCheckOut() {
        SearchRequest eq = new SearchRequest("h", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 1), List.of(1));
        assertEquals(false, eq.isDateRangeValid());
    }

    @Test
    void validatorDetectsBlankHotelIdNullDatesAndEmptyAges() {
        SearchRequest req = new SearchRequest(" ", null, null, List.of());
        Set<ConstraintViolation<SearchRequest>> v = validator.validate(req);

        assertAll(
                () -> assertTrue(v.size() >= 3),
                () -> assertTrue(v.stream().anyMatch(c -> c.getMessage().contains("hotelId"))),
                () -> assertTrue(v.stream().anyMatch(c -> c.getMessage().contains("ages")))
        );
    }

    @Test
    void validatorDetectsNegativeAge() {
        SearchRequest req = new SearchRequest("h",
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2),
                Arrays.asList(1, -1));
        Set<ConstraintViolation<SearchRequest>> v = validator.validate(req);
        assertTrue(v.stream().anyMatch(c -> c.getMessage().contains("zero")));
    }

    @Test
    void recordCompactConstructorHandlesNullAgesList() {
        SearchRequest req = new SearchRequest("h", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2), null);
        assertAll(
                () -> assertNotNull(req.ages()),
                () -> assertTrue(req.ages().isEmpty())
        );
    }

    @Test
    void recordCompactConstructorMakesAgesImmutable() {
        List<Integer> mutable = new ArrayList<>(Arrays.asList(1, 2));
        SearchRequest req = new SearchRequest("h", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2), mutable);
        mutable.add(99);

        assertAll(
                () -> assertEquals(List.of(1, 2), req.ages()),
                () -> assertThrows(UnsupportedOperationException.class, () -> req.ages().add(7))
        );
    }

    @Test
    void jacksonParsesDatesInExpectedFormat() throws JsonProcessingException {
        ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        String json = """
                {"hotelId":"h","checkIn":"29/12/2023","checkOut":"31/12/2023","ages":[1]}
                """;
        SearchRequest req = mapper.readValue(json, SearchRequest.class);
        assertAll(
                () -> assertEquals(LocalDate.of(2023, 12, 29), req.checkIn()),
                () -> assertEquals(LocalDate.of(2023, 12, 31), req.checkOut())
        );
    }
}
