package com.mindata.hotelavailability.domain.port.out;

import com.mindata.hotelavailability.domain.model.HotelSearch;

public interface SearchEventPublisher {

    void publish(HotelSearch search);
}
