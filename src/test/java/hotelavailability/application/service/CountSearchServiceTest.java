package hotelavailability.application.service;

import com.mindata.hotelavailability.application.service.CountSearchService;
import com.mindata.hotelavailability.domain.exception.SearchNotFoundException;
import com.mindata.hotelavailability.domain.model.HotelSearch;
import com.mindata.hotelavailability.domain.port.in.CountSearchUseCase.CountResult;
import com.mindata.hotelavailability.domain.port.out.SearchRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CountSearchServiceTest {

    private final HotelSearch sample = new HotelSearch(
            "abc", "hotel", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2), List.of(1, 2));

    @Test
    void returnsOriginalSearchWithCount() {
        SearchRepository repo = new SearchRepository() {
            @Override public void save(HotelSearch s) { }
            @Override public Optional<HotelSearch> findById(String id) {
                return "abc".equals(id) ? Optional.of(sample) : Optional.empty();
            }
            @Override public long countEqual(HotelSearch s) { return 7L; }
        };
        CountSearchService service = new CountSearchService(repo);

        CountResult result = service.countBySearchId("abc");

        assertAll(
                () -> assertSame(sample, result.search()),
                () -> assertEquals(7L, result.count())
        );
    }

    @Test
    void throwsWhenNotFound() {
        SearchRepository repo = new SearchRepository() {
            @Override public void save(HotelSearch s) { }
            @Override public Optional<HotelSearch> findById(String id) { return Optional.empty(); }
            @Override public long countEqual(HotelSearch s) { return 0L; }
        };
        CountSearchService service = new CountSearchService(repo);
        assertThrows(SearchNotFoundException.class, () -> service.countBySearchId("zzz"));
    }

    @Test
    void rejectsBlankOrNullSearchId() {
        SearchRepository repo = new SearchRepository() {
            @Override public void save(HotelSearch s) { }
            @Override public Optional<HotelSearch> findById(String id) { return Optional.empty(); }
            @Override public long countEqual(HotelSearch s) { return 0; }
        };
        CountSearchService service = new CountSearchService(repo);
        assertAll(
                () -> assertThrows(SearchNotFoundException.class, () -> service.countBySearchId(null)),
                () -> assertThrows(SearchNotFoundException.class, () -> service.countBySearchId(" "))
        );
    }
}
