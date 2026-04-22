package com.mindata.hotelavailability.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "HOTEL_SEARCH", indexes = {
        @Index(name = "IDX_HOTEL_SEARCH_EQUALITY",
               columnList = "HOTEL_ID, CHECK_IN, CHECK_OUT, AGES_CSV")
})
public class SearchEntity {

    @Id
    @Column(name = "SEARCH_ID", length = 64, nullable = false, updatable = false)
    private String searchId;

    @Column(name = "HOTEL_ID", length = 128, nullable = false)
    private String hotelId;

    @Column(name = "CHECK_IN", nullable = false)
    private LocalDate checkIn;

    @Column(name = "CHECK_OUT", nullable = false)
    private LocalDate checkOut;

    @Column(name = "AGES_CSV", length = 1024, nullable = false)
    private String agesCsv;

    protected SearchEntity() {
    }

    public SearchEntity(String searchId, String hotelId, LocalDate checkIn, LocalDate checkOut, String agesCsv) {
        this.searchId = searchId;
        this.hotelId = hotelId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.agesCsv = agesCsv;
    }

    public String getSearchId() {
        return searchId;
    }

    public String getHotelId() {
        return hotelId;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public String getAgesCsv() {
        return agesCsv;
    }
}
