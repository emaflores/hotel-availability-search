package com.mindata.hotelavailability.domain.port.in;

import java.time.LocalDate;
import java.util.List;

public interface CreateSearchUseCase {

    String createSearch(CreateSearchCommand command);

    record CreateSearchCommand(
            String hotelId,
            LocalDate checkIn,
            LocalDate checkOut,
            List<Integer> ages
    ) {
        public CreateSearchCommand {
            ages = ages == null ? List.of() : List.copyOf(ages);
        }
    }
}
