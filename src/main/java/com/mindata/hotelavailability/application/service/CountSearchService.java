package com.mindata.hotelavailability.application.service;

import com.mindata.hotelavailability.domain.exception.SearchNotFoundException;
import com.mindata.hotelavailability.domain.model.HotelSearch;
import com.mindata.hotelavailability.domain.port.in.CountSearchUseCase;
import com.mindata.hotelavailability.domain.port.out.SearchRepository;

public class CountSearchService implements CountSearchUseCase {

    private final SearchRepository searchRepository;

    public CountSearchService(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @Override
    public CountResult countBySearchId(String searchId) {
        if (searchId == null || searchId.isBlank()) {
            throw new SearchNotFoundException(String.valueOf(searchId));
        }
        HotelSearch search = searchRepository.findById(searchId)
                .orElseThrow(() -> new SearchNotFoundException(searchId));
        long count = searchRepository.countEqual(search);
        return new CountResult(search, count);
    }
}
