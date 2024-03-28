package com.GOBookingAPI.payload.response;

import com.GOBookingAPI.payload.dto.BookingStatistic;
import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingStatisticResponse {
    private int totalBookings;
    private Integer completedCount;
    private Integer cancelledCount;
    private Long totalAmount;
    private Long driverReceiveAmount;
    private Integer quantityCancelByDriver;
    private Integer quantityCancelByCustomer;
    private Integer rating5Count;
    private Integer rating4Count;
    private Integer rating3Count;
    private Integer rating2Count;
    private Integer rating1Count;
    private Integer rating0Count;

//	"    COUNT(*) AS total, " +
//            "    COALESCE(SUM(CASE WHEN booking.status = 'COMPLETE' THEN 1 END), 0) AS completeCount, " +
//            "    COALESCE(SUM(CASE WHEN booking.status = 'COMPLETE' THEN booking.amount END), 0) AS quantityComplete," +
//            "    COALESCE(SUM(CASE WHEN ((booking.status = 'CANCELLED' OR booking.status = 'WAITING_REFUND' OR booking.status = 'REFUNDED') AND booking.reason_type = 'DRIVER') THEN 1 END), 0) AS quantityCancelByDriver, " +
//            "    COALESCE(SUM(CASE WHEN review.rating = '5' THEN 1 END), 0) AS rating5Count," +
//            "    COALESCE(SUM(CASE WHEN review.rating = '4' THEN 1 END), 0) AS rating4Count," +
//            "    COALESCE(SUM(CASE WHEN review.rating = '3' THEN 1 END), 0) AS rating3Count," +
//            "    COALESCE(SUM(CASE WHEN review.rating = '2' THEN 1 END), 0) AS rating2Count," +
//            "    COALESCE(SUM(CASE WHEN review.rating = '1' THEN 1 END), 0) AS rating1Count," +
//            "    COALESCE(SUM(CASE WHEN review.rating = '0' THEN 1 END), 0) AS rating0Count" +

    public BookingStatisticResponse(BookingStatistic bookingStatistic, double percent){
        this.totalBookings = bookingStatistic.getTotal();
        this.completedCount = bookingStatistic.getCompleteCount();
        this.totalAmount = bookingStatistic.getTotalAmount();
        this.quantityCancelByDriver = bookingStatistic.getQuantityCancelByDriver();
        this.rating5Count = bookingStatistic.getRating5Count();
        this.rating4Count = bookingStatistic.getRating4Count();
        this.rating3Count = bookingStatistic.getRating3Count();
        this.rating2Count = bookingStatistic.getRating2Count();
        this.rating1Count = bookingStatistic.getRating1Count();
        this.rating0Count = bookingStatistic.getRating0Count();
        this.cancelledCount = this.totalBookings - this.completedCount;
        this.quantityCancelByCustomer = this.cancelledCount - this.quantityCancelByDriver;
        this.driverReceiveAmount = Math.round(percent * this.totalAmount);
    }
}
