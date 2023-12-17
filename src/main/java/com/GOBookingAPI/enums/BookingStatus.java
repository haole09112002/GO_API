package com.GOBookingAPI.enums;

public enum BookingStatus {
	COMPLETE,						// Chuyến đi đã hoàn tất, tài xế và khách hàng đã xác nhận
	CANCELLED,						// Chuyến đi đã bị hủy
	ON_RIDE,						// Tài xế và khách hàng đang trên đường di chuyển đến điểm đến
	WAITING,						// Khách hàng đã đặt xe và đang chờ thanh toán
	PAID,							// Chuyến đi đã thanh toán và dang tim tai xe
	REFUNDED,						// Chuyến đi đã được hoàn tiền
	WAITING_REFUND,					// chuyến đi dang chờ hoàn tiền
	FOUND,							// Đã tìm thấy tài xế và chờ tài xế đến
	ARRIVED_PICKUP					// Tài xế đã đến điểm đón
}
/*
    @author: HaoLV
    @description: WAITING, PAID, FOUND, ARRIVED_PICKUP , ON_RIDE, COMPLETE
    Customer action:
		=> cancel: WAITING, CANCELLED
		=> cancel: WAITING, PAID, WAITING_REFUND, REFUNDED
		=> cancel: WAITING, PAID, FOUND, WAITING_REFUND, REFUNDED
*/