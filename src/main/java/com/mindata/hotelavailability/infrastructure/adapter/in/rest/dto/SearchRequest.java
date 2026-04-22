package com.mindata.hotelavailability.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mindata.hotelavailability.domain.port.in.CreateSearchUseCase.CreateSearchCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Payload for creating a hotel availability search")
public record SearchRequest(

        @Schema(example = "1234aBc", description = "Hotel identifier")
        @NotBlank(message = "hotelId must not be null or blank")
        String hotelId,

        @Schema(example = "29/12/2023", description = "Check-in date (dd/MM/yyyy)")
        @NotNull(message = "checkIn must not be null")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        LocalDate checkIn,

        @Schema(example = "31/12/2023", description = "Check-out date (dd/MM/yyyy)")
        @NotNull(message = "checkOut must not be null")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        LocalDate checkOut,

        @Schema(example = "[30, 29, 1, 3]", description = "Guest ages. Order is significant for counting.")
        @NotEmpty(message = "ages must not be null or empty")
        List<@NotNull(message = "ages must not contain null values")
             @PositiveOrZero(message = "ages must be greater than or equal to zero") Integer> ages
) {

    public SearchRequest {
        ages = ages == null ? List.of() : List.copyOf(ages);
    }

    @AssertTrue(message = "checkIn must be strictly before checkOut")
    @Schema(hidden = true)
    public boolean isDateRangeValid() {
        if (checkIn == null || checkOut == null) {
            return true;
        }
        return checkIn.isBefore(checkOut);
    }

    public CreateSearchCommand toCommand() {
        return new CreateSearchCommand(hotelId, checkIn, checkOut, ages);
    }
}
