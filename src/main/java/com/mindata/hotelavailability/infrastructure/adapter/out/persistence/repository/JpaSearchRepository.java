package com.mindata.hotelavailability.infrastructure.adapter.out.persistence.repository;

import com.mindata.hotelavailability.infrastructure.adapter.out.persistence.entity.SearchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface JpaSearchRepository extends JpaRepository<SearchEntity, String> {

    @Query("""
           SELECT COUNT(s)
             FROM SearchEntity s
            WHERE s.hotelId  = :hotelId
              AND s.checkIn  = :checkIn
              AND s.checkOut = :checkOut
              AND s.agesCsv  = :agesCsv
           """)
    long countEqual(
            @Param("hotelId") String hotelId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("agesCsv") String agesCsv);
}
