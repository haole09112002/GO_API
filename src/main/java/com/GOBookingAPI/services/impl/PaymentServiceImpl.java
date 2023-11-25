package com.GOBookingAPI.services.impl;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Payment;
import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.enums.BookingStatus;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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



    @Override
    @Transactional
    public void handlePaymentTransaction(PaymentRequest req) {      // callback from VNpay
        //todo valid data;
        User user = userRepository.findByEmail(req.getEmail()).orElseThrow(() -> new NotFoundException("Không tìm thấy khách hàng"));
        Booking booking = bookingRepository.findById(req.getBookingId()).orElseThrow(() -> new NotFoundException("Không tìm thấy booking id: " + req.getBookingId()));
        if(user.getId() != booking.getCustomer().getUser().getId())
            throw  new AccessDeniedException("Booking không thuộc về user id: " + user.getId());
        if(booking.getStatus() != BookingStatus.WAITING){
            throw  new BadRequestException("Booking không ở trạng thái cần thanh toán");
        }
        booking.setStatus(BookingStatus.PAID);
        bookingRepository.save(booking);
        Payment payment = new Payment();
        payment.setAmount(req.getAmount());
        payment.setTransactionId(req.getTransactionId());
        payment.setTimeStamp(req.getTimeStamp());
        payment.setCustomer(user.getCustomer());
        payment.setBooking(booking);
        paymentRepository.save(payment);
        //todo sendRequestChangeBookingStatus => BookingStatus.PAID for customer
        webSocketService.notifyBookingStatus(user.getId(), new BookingStatusResponse(booking.getId(), booking.getStatus()));
        //todo sendRequestDriverLocation for all driver free
        driverService.scheduleFindDriverTask(booking.getId());      //todo
    }
}
