package com.mindata.hotelavailability.infrastructure.adapter.out.persistence;

import com.mindata.hotelavailability.domain.model.HotelSearch;
import com.mindata.hotelavailability.infrastructure.adapter.out.persistence.SearchRepositoryAdapter;
import com.mindata.hotelavailability.infrastructure.adapter.out.persistence.repository.JpaSearchRepository;
import com.mindata.hotelavailability.infrastructure.config.OracleTestcontainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({SearchRepositoryAdapter.class, OracleTestcontainersConfig.class})
class SearchRepositoryAdapterTest {

    @Autowired
    private SearchRepositoryAdapter adapter;

    @Autowired
    private JpaSearchRepository jpaRepository;

    @Test
    void saveAndFindById() {
        HotelSearch search = new HotelSearch(
                "id-1", "hotel", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2), List.of(30, 29, 1, 3));

        adapter.save(search);
        Optional<HotelSearch> loaded = adapter.findById("id-1");

        assertAll(
                () -> assertTrue(loaded.isPresent()),
                () -> assertEquals("hotel", loaded.get().hotelId()),
                () -> assertEquals(List.of(30, 29, 1, 3), loaded.get().ages())
        );
    }

    @Test
    void countEqualIsOrderSensitive() {
        HotelSearch original = new HotelSearch(
                "a", "h", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2), List.of(30, 29, 1, 3));
        HotelSearch sameOrder = new HotelSearch(
                "b", "h", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2), List.of(30, 29, 1, 3));
        HotelSearch differentOrder = new HotelSearch(
                "c", "h", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2), List.of(3, 29, 30, 1));

        adapter.save(original);
        adapter.save(sameOrder);
        adapter.save(differentOrder);

        assertAll(
                () -> assertEquals(2L, adapter.countEqual(original)),
                () -> assertEquals(1L, adapter.countEqual(differentOrder)),
                () -> assertEquals(3L, jpaRepository.count())
        );
    }

    @Test
    void findByIdReturnsEmptyForUnknown() {
        assertTrue(adapter.findById("unknown").isEmpty());
    }

    @Test
    void encodeAndDecodeAgesUseStringJoinerAndSplit() {
        String csv = SearchRepositoryAdapter.encodeAges(List.of(30, 29, 1, 3));
        List<Integer> decoded = SearchRepositoryAdapter.decodeAges(csv);

        assertAll(
                () -> assertEquals("30,29,1,3", csv),
                () -> assertEquals(List.of(30, 29, 1, 3), decoded),
                () -> assertEquals(List.of(), SearchRepositoryAdapter.decodeAges("")),
                () -> assertEquals(List.of(), SearchRepositoryAdapter.decodeAges(null))
        );
    }
}
