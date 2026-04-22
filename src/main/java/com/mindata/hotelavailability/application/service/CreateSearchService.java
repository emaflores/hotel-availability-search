package com.mindata.hotelavailability.application.service;

import com.mindata.hotelavailability.domain.model.HotelSearch;
import com.mindata.hotelavailability.domain.port.in.CreateSearchUseCase;
import com.mindata.hotelavailability.domain.port.out.SearchEventPublisher;

import java.util.UUID;

public class CreateSearchService implements CreateSearchUseCase {

    private final SearchEventPublisher eventPublisher;

    public CreateSearchService(SearchEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public String createSearch(CreateSearchCommand command) {
        String searchId = UUID.randomUUID().toString();
        HotelSearch search = new HotelSearch(
                searchId,
                command.hotelId(),
                command.checkIn(),
                command.checkOut(),
                command.ages()
        );
        eventPublisher.publish(search);
        return searchId;
    }
}
