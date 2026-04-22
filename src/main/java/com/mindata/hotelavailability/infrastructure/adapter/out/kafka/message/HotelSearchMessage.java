package com.mindata.hotelavailability.infrastructure.adapter.out.kafka.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mindata.hotelavailability.domain.model.HotelSearch;

import java.time.LocalDate;
import java.util.List;

public record HotelSearchMessage(
        String searchId,
        String hotelId,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy") LocalDate checkIn,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy") LocalDate checkOut,
        List<Integer> ages
) {

    public HotelSearchMessage {
        ages = ages == null ? List.of() : List.copyOf(ages);
    }

    public static HotelSearchMessage from(HotelSearch search) {
        return new HotelSearchMessage(
                search.searchId(),
                search.hotelId(),
                search.checkIn(),
                search.checkOut(),
                search.ages()
        );
    }

    public HotelSearch toDomain() {
        return new HotelSearch(searchId, hotelId, checkIn, checkOut, ages);
    }
}
