package com.GOBookingAPI.payload.dto;

public interface BookingStatistic {

    Integer getTotal();

    Integer getCompleteCount();

    Long getTotalAmount();

    Integer getQuantityCancelByDriver();

    Integer getRating5Count();

    Integer getRating4Count();

    Integer getRating3Count();

    Integer getRating2Count();

    Integer getRating1Count();

    Integer getRating0Count();
}