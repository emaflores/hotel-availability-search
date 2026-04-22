package hotelavailability.infrastructure.adapter.in.rest.dto;

import com.mindata.hotelavailability.domain.model.HotelSearch;
import com.mindata.hotelavailability.domain.port.in.CountSearchUseCase.CountResult;
import com.mindata.hotelavailability.infrastructure.adapter.in.rest.dto.CountResponse;
import com.mindata.hotelavailability.infrastructure.adapter.in.rest.dto.ErrorResponse;
import com.mindata.hotelavailability.infrastructure.adapter.in.rest.dto.SearchResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DtoRecordsTest {

    @Test
    void searchResponseCarriesSearchId() {
        assertEquals("abc", new SearchResponse("abc").searchId());
    }

    @Test
    void errorResponseFactoriesNormalizeDetails() {
        ErrorResponse a = ErrorResponse.of(400, "Bad Request", "boom");
        ErrorResponse b = ErrorResponse.of(400, "Bad Request", "boom", List.of("x", "y"));
        ErrorResponse c = new ErrorResponse(null, 400, "X", "m", null);

        assertAll(
                () -> assertEquals(List.of(), a.details()),
                () -> assertEquals(List.of("x", "y"), b.details()),
                () -> assertEquals(List.of(), c.details()),
                () -> assertThrows(UnsupportedOperationException.class, () -> b.details().add("z"))
        );
    }

    @Test
    void countResponseFromMapsDomainModel() {
        HotelSearch search = new HotelSearch(
                "id-1", "h", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2), List.of(1, 2));
        CountResponse resp = CountResponse.from(new CountResult(search, 3L));

        assertAll(
                () -> assertEquals("id-1", resp.searchId()),
                () -> assertEquals("h", resp.search().hotelId()),
                () -> assertEquals(3L, resp.count()),
                () -> assertEquals(List.of(1, 2), resp.search().ages())
        );
    }

    @Test
    void searchBodyDefensivelyCopiesAgesAndHandlesNull() {
        List<Integer> mutable = new ArrayList<>(Arrays.asList(1, 2));
        CountResponse.SearchBody body = new CountResponse.SearchBody(
                "h", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2), mutable);
        mutable.add(99);

        CountResponse.SearchBody nullBody = new CountResponse.SearchBody(
                "h", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2), null);

        assertAll(
                () -> assertEquals(List.of(1, 2), body.ages()),
                () -> assertTrue(nullBody.ages().isEmpty()),
                () -> assertThrows(UnsupportedOperationException.class, () -> body.ages().add(7))
        );
    }
}
