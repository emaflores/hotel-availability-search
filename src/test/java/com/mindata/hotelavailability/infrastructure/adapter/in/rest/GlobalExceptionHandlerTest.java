package com.mindata.hotelavailability.infrastructure.adapter.in.rest;

import com.mindata.hotelavailability.domain.exception.InvalidSearchException;
import com.mindata.hotelavailability.domain.exception.SearchNotFoundException;
import com.mindata.hotelavailability.infrastructure.adapter.in.rest.GlobalExceptionHandler;
import com.mindata.hotelavailability.infrastructure.adapter.in.rest.dto.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handlesMethodArgumentNotValidWithDefaultAndNullMessages() throws Exception {
        BeanPropertyBindingResult binding = new BeanPropertyBindingResult(new Object(), "req");
        binding.addError(new ObjectError("req", "field X invalid"));
        binding.addError(new ObjectError("req", (String[]) null, null, null));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                mock(org.springframework.core.MethodParameter.class), binding);

        ResponseEntity<ErrorResponse> resp = handler.handleBodyValidation(ex);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode()),
                () -> assertEquals(2, resp.getBody().details().size()),
                () -> assertEquals("Bad Request", resp.getBody().error())
        );
    }

    @Test
    void handlesConstraintViolation() {
        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("must not be blank");
        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

        ResponseEntity<ErrorResponse> resp = handler.handleConstraintViolation(ex);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode()),
                () -> assertEquals(1, resp.getBody().details().size())
        );
    }

    @Test
    void handlesMissingParameter() {
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("searchId", "String");

        ResponseEntity<ErrorResponse> resp = handler.handleMissingParam(ex);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode()),
                () -> assertNotNull(resp.getBody().message())
        );
    }

    @Test
    void handlesUnreadableMessage() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException(
                "broken", new MockHttpInputMessage(new byte[0]));

        ResponseEntity<ErrorResponse> resp = handler.handleUnreadable(ex);

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    void handlesDomainInvariant() {
        ResponseEntity<ErrorResponse> resp = handler.handleDomainInvariant(
                new InvalidSearchException("bad"));
        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode()),
                () -> assertEquals("bad", resp.getBody().message())
        );
    }

    @Test
    void handlesNotFound() {
        ResponseEntity<ErrorResponse> resp = handler.handleNotFound(new SearchNotFoundException("z"));
        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode()),
                () -> assertEquals("Not Found", resp.getBody().error())
        );
    }

    @Test
    void handlesUnexpected() {
        ResponseEntity<ErrorResponse> resp = handler.handleUnexpected(new RuntimeException("boom"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
    }
}
