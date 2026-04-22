package com.mindata.hotelavailability.domain.port.out;

import com.mindata.hotelavailability.domain.model.HotelSearch;

import java.util.Optional;

public interface SearchRepository {

    void save(HotelSearch search);

    Optional<HotelSearch> findById(String searchId);

    long countEqual(HotelSearch search);
}
