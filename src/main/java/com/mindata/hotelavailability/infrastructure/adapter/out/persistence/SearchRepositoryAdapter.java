package com.mindata.hotelavailability.infrastructure.adapter.out.persistence;

import com.mindata.hotelavailability.domain.model.HotelSearch;
import com.mindata.hotelavailability.domain.port.out.SearchRepository;
import com.mindata.hotelavailability.infrastructure.adapter.out.persistence.entity.SearchEntity;
import com.mindata.hotelavailability.infrastructure.adapter.out.persistence.repository.JpaSearchRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

@Component
public class SearchRepositoryAdapter implements SearchRepository {

    static final String AGES_DELIMITER = ",";

    private final JpaSearchRepository jpaRepository;

    public SearchRepositoryAdapter(JpaSearchRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public void save(HotelSearch search) {
        SearchEntity entity = new SearchEntity(
                search.searchId(),
                search.hotelId(),
                search.checkIn(),
                search.checkOut(),
                encodeAges(search.ages())
        );
        jpaRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HotelSearch> findById(String searchId) {
        return jpaRepository.findById(searchId).map(SearchRepositoryAdapter::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public long countEqual(HotelSearch search) {
        return jpaRepository.countEqual(
                search.hotelId(),
                search.checkIn(),
                search.checkOut(),
                encodeAges(search.ages())
        );
    }

    static String encodeAges(List<Integer> ages) {
        StringJoiner joiner = new StringJoiner(AGES_DELIMITER);
        for (Integer age : ages) {
            joiner.add(Integer.toString(age));
        }
        return joiner.toString();
    }

    static List<Integer> decodeAges(String csv) {
        if (csv == null || csv.isEmpty()) {
            return List.of();
        }
        String[] tokens = csv.split(AGES_DELIMITER);
        Integer[] values = new Integer[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            values[i] = Integer.valueOf(tokens[i]);
        }
        return List.of(values);
    }

    private static HotelSearch toDomain(SearchEntity entity) {
        return new HotelSearch(
                entity.getSearchId(),
                entity.getHotelId(),
                entity.getCheckIn(),
                entity.getCheckOut(),
                decodeAges(entity.getAgesCsv())
        );
    }
}
