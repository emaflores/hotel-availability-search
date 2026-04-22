package com.mindata.hotelavailability.domain.model;

import com.mindata.hotelavailability.domain.exception.InvalidSearchException;
import com.mindata.hotelavailability.domain.model.HotelSearch;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HotelSearchTest {

    private static final LocalDate IN = LocalDate.of(2023, 12, 29);
    private static final LocalDate OUT = LocalDate.of(2023, 12, 31);

    @Test
    void createsValidSearch() {
        HotelSearch search = new HotelSearch("id-1", "hotel-1", IN, OUT, List.of(30, 29, 1, 3));

        assertAll(
                () -> assertEquals("id-1", search.searchId()),
                () -> assertEquals("hotel-1", search.hotelId()),
                () -> assertEquals(IN, search.checkIn()),
                () -> assertEquals(OUT, search.checkOut()),
                () -> assertEquals(List.of(30, 29, 1, 3), search.ages())
        );
    }

    @Test
    void storesAnImmutableDefensiveCopyOfAges() {
        List<Integer> mutable = new ArrayList<>(Arrays.asList(1, 2, 3));
        HotelSearch search = new HotelSearch("id-1", "hotel-1", IN, OUT, mutable);

        mutable.add(999);

        assertAll(
                () -> assertEquals(List.of(1, 2, 3), search.ages()),
                () -> assertNotSame(mutable, search.ages()),
                () -> assertThrows(UnsupportedOperationException.class, () -> search.ages().add(42))
        );
    }

    @Test
    void rejectsBlankOrNullSearchId() {
        assertAll(
                () -> assertThrows(InvalidSearchException.class,
                        () -> new HotelSearch(null, "h", IN, OUT, List.of(1))),
                () -> assertThrows(InvalidSearchException.class,
                        () -> new HotelSearch(" ", "h", IN, OUT, List.of(1)))
        );
    }

    @Test
    void rejectsBlankOrNullHotelId() {
        assertAll(
                () -> assertThrows(InvalidSearchException.class,
                        () -> new HotelSearch("id", null, IN, OUT, List.of(1))),
                () -> assertThrows(InvalidSearchException.class,
                        () -> new HotelSearch("id", "", IN, OUT, List.of(1)))
        );
    }

    @Test
    void rejectsNullDates() {
        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> new HotelSearch("id", "h", null, OUT, List.of(1))),
                () -> assertThrows(NullPointerException.class,
                        () -> new HotelSearch("id", "h", IN, null, List.of(1)))
        );
    }

    @Test
    void rejectsCheckInNotBeforeCheckOut() {
        assertAll(
                () -> {
                    InvalidSearchException eq = assertThrows(InvalidSearchException.class,
                            () -> new HotelSearch("id", "h", IN, IN, List.of(1)));
                    assertTrue(eq.getMessage().contains("checkIn"));
                },
                () -> assertThrows(InvalidSearchException.class,
                        () -> new HotelSearch("id", "h", OUT, IN, List.of(1)))
        );
    }

    @Test
    void rejectsNullOrEmptyAges() {
        assertAll(
                () -> assertThrows(InvalidSearchException.class,
                        () -> new HotelSearch("id", "h", IN, OUT, null)),
                () -> assertThrows(InvalidSearchException.class,
                        () -> new HotelSearch("id", "h", IN, OUT, List.of()))
        );
    }

    @Test
    void rejectsNullOrNegativeAgeElements() {
        List<Integer> withNull = new ArrayList<>();
        withNull.add(1);
        withNull.add(null);
        assertAll(
                () -> assertThrows(InvalidSearchException.class,
                        () -> new HotelSearch("id", "h", IN, OUT, withNull)),
                () -> assertThrows(InvalidSearchException.class,
                        () -> new HotelSearch("id", "h", IN, OUT, List.of(1, -1, 2)))
        );
    }
}
