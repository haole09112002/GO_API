package com.GOBookingAPI.services.impl;

import com.GOBookingAPI.config.VNPayConfig;
import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Payment;
import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.enums.WebSocketBookingTitle;
import com.GOBookingAPI.exceptions.AccessDeniedException;
import com.GOBookingAPI.exceptions.BadRequestException;
import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.request.PaymentRequest;
import com.GOBookingAPI.payload.response.BookingStatusResponse;
import com.GOBookingAPI.repositories.BookingRepository;
import com.GOBookingAPI.repositories.PaymentRepository;
import com.GOBookingAPI.repositories.UserRepository;
import com.GOBookingAPI.services.IDriverService;
import com.GOBookingAPI.services.IPaymentService;
import com.GOBookingAPI.services.IWebSocketService;
import com.GOBookingAPI.utils.LocationDriver;
import com.GOBookingAPI.utils.ManagerLocation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Timestamp;
import java.sql.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class PaymentServiceImpl implements IPaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private IWebSocketService webSocketService;

    @Autowired
    private IDriverService driverService;

    @Autowired
    private ManagerLocation managerLocation;


    @Override
    @Transactional
    public void handlePaymentTransaction(Map<String, String> req) {      // callback from VNpay
        //todo valid data;
    	System.out.println(req.get("email"));
        User user = userRepository.findByEmail(req.get("email")).orElseThrow(() -> new NotFoundException("Không tìm thấy khách hàng"));
        Booking booking = bookingRepository.findById(Integer.parseInt(req.get("bookingId"))).orElseThrow(() -> new NotFoundException("Không tìm thấy booking id: " + Integer.parseInt(req.get("bookingId"))));
        if(user.getId() != booking.getCustomer().getUser().getId())
            throw  new AccessDeniedException("Booking không thuộc về user id: " + user.getId());
        if(booking.getStatus() != BookingStatus.WAITING){
            throw  new BadRequestException("Booking không ở trạng thái cần thanh toán");
        }
        // check gia tien doi chieu 
        String vnp_SecureHash  = req.get("vnp_SecureHash");
        req.remove("vnp_SecureHash");
        String signValue = VNPayConfig.hashAllFields(req);
        if(signValue.equals(vnp_SecureHash)) {
        	
        	if("00".equals(req.get("vnp_ResponseCode"))) {
        		  booking.setStatus(BookingStatus.PAID);
                  bookingRepository.save(booking);
                  Payment payment = new Payment();
                  payment.setAmount(Double.valueOf(req.get("vnp_Amount")));
                  payment.setTransactionId(req.get("vnp_TransactionNo"));
                  payment.setTimeStamp(Date.valueOf(req.get("")));
                  payment.setCustomer(user.getCustomer());
                  payment.setBooking(booking);
                  paymentRepository.save(payment);
                  //todo sendRequestChangeBookingStatus => BookingStatus.PAID for customer
                  webSocketService.notifyBookingStatusToCustomer(user.getId(), new BookingStatusResponse(booking.getId(), booking.getStatus()));
                  //todo sendRequestDriverLocation for all driver free
                  List<LocationDriver> locationDrivers = managerLocation.getByStatus(WebSocketBookingTitle.FREE.toString());
                  for(LocationDriver locaDriver : locationDrivers) {
                  	webSocketService.notifytoDriver(locaDriver.getIddriver(), "HAVEBOOKING");
                  }
                  driverService.scheduleFindDriverTask(booking.getId(),booking.getPickupLocation());      //todo	
        	}else {
        		throw new BadRequestException("Thanh toán không thành công !");
        	}
        	 
        }else {
        	throw new BadRequestException("Chử ký không hợp lệ!");
        }
     
    }
    
}
