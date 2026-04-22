package com.mindata.hotelavailability.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mindata.hotelavailability.domain.port.in.CreateSearchUseCase.CreateSearchCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Schema(description = "Payload for creating a hotel availability search")
public record SearchRequest(

        @Schema(example = "1234aBc", description = "Hotel identifier")
        @NotBlank(message = "hotelId must not be null or blank")
        @Size(max = 64, message = "hotelId must be at most 64 characters")
        String hotelId,

        @Schema(example = "29/12/2023", description = "Check-in date (dd/MM/yyyy)")
        @NotNull(message = "checkIn must not be null")
        @FutureOrPresent(message = "checkIn must not be in the past")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        LocalDate checkIn,

        @Schema(example = "31/12/2023", description = "Check-out date (dd/MM/yyyy)")
        @NotNull(message = "checkOut must not be null")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        LocalDate checkOut,

        @Schema(example = "[30, 29, 1, 3]", description = "Guest ages. Order is significant for counting.")
        @NotEmpty(message = "ages must not be null or empty")
        @Size(max = 20, message = "ages must contain at most 20 elements")
        List<@NotNull(message = "ages must not contain null values")
             @PositiveOrZero(message = "ages must be greater than or equal to zero")
             @Max(value = 120, message = "ages must be less than or equal to 120") Integer> ages
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

    @AssertTrue(message = "range between checkIn and checkOut must not exceed 30 days")
    @Schema(hidden = true)
    public boolean isDateRangeWithinMax() {
        if (checkIn == null || checkOut == null || !checkIn.isBefore(checkOut)) {
            return true;
        }
        return ChronoUnit.DAYS.between(checkIn, checkOut) <= 30;
    }

    @AssertTrue(message = "checkIn must be within one year from today")
    @Schema(hidden = true)
    public boolean isCheckInWithinBookingWindow() {
        if (checkIn == null) {
            return true;
        }
        return !checkIn.isAfter(LocalDate.now().plusYears(1));
    }

    public CreateSearchCommand toCommand() {
        return new CreateSearchCommand(hotelId, checkIn, checkOut, ages);
    }
}
