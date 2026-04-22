package com.mindata.hotelavailability.domain.port.in;

import com.mindata.hotelavailability.domain.model.HotelSearch;

public interface CountSearchUseCase {

    CountResult countBySearchId(String searchId);

    record CountResult(HotelSearch search, long count) {}
}
