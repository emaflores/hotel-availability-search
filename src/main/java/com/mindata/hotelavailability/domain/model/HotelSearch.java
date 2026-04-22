package com.mindata.hotelavailability.domain.model;

import com.mindata.hotelavailability.domain.exception.InvalidSearchException;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record HotelSearch(
        String searchId,
        String hotelId,
        LocalDate checkIn,
        LocalDate checkOut,
        List<Integer> ages
) {

    public HotelSearch {
        if (searchId == null || searchId.isBlank()) {
            throw new InvalidSearchException("searchId must not be null or blank");
        }
        if (hotelId == null || hotelId.isBlank()) {
            throw new InvalidSearchException("hotelId must not be null or blank");
        }
        Objects.requireNonNull(checkIn, "checkIn must not be null");
        Objects.requireNonNull(checkOut, "checkOut must not be null");
        if (!checkIn.isBefore(checkOut)) {
            throw new InvalidSearchException("checkIn must be strictly before checkOut");
        }
        if (ages == null || ages.isEmpty()) {
            throw new InvalidSearchException("ages must not be null or empty");
        }
        for (Integer age : ages) {
            if (age == null) {
                throw new InvalidSearchException("ages must not contain null values");
            }
            if (age < 0) {
                throw new InvalidSearchException("ages must be greater than or equal to zero");
            }
        }
        ages = List.copyOf(ages);
    }
}
