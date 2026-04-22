package com.mindata.hotelavailability.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mindata.hotelavailability.domain.model.HotelSearch;
import com.mindata.hotelavailability.domain.port.in.CountSearchUseCase.CountResult;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Response for GET /count")
public record CountResponse(
        String searchId,
        SearchBody search,
        long count
) {

    public static CountResponse from(CountResult result) {
        HotelSearch s = result.search();
        return new CountResponse(
                s.searchId(),
                new SearchBody(s.hotelId(), s.checkIn(), s.checkOut(), s.ages()),
                result.count()
        );
    }

    public record SearchBody(
            String hotelId,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy") LocalDate checkIn,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy") LocalDate checkOut,
            List<Integer> ages
    ) {
        public SearchBody {
            ages = ages == null ? List.of() : List.copyOf(ages);
        }
    }
}
