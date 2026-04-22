package com.mindata.hotelavailability.domain.exception;

import com.mindata.hotelavailability.domain.exception.InvalidSearchException;
import com.mindata.hotelavailability.domain.exception.SearchNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainExceptionsTest {

    @Test
    void invalidSearchCarriesMessage() {
        InvalidSearchException ex = new InvalidSearchException("bad field");
        assertEquals("bad field", ex.getMessage());
    }

    @Test
    void searchNotFoundIncludesId() {
        SearchNotFoundException ex = new SearchNotFoundException("abc");
        assertAll(
                () -> assertTrue(ex.getMessage().contains("abc")),
                () -> assertTrue(ex.getMessage().toLowerCase().contains("not found"))
        );
    }
}
