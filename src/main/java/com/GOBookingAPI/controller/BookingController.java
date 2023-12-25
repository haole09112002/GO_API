package com.GOBookingAPI.controller;


import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.enums.RoleEnum;
import com.GOBookingAPI.exceptions.AccessDeniedException;
import com.GOBookingAPI.payload.request.BookingStatusRequest;
import com.GOBookingAPI.payload.response.BookingCancelResponse;
import com.GOBookingAPI.payload.response.BookingStatusResponse;
import com.GOBookingAPI.services.IPaymentService;
import com.GOBookingAPI.services.IUserService;
import com.GOBookingAPI.services.IWebSocketService;
import com.GOBookingAPI.utils.AppConstants;
import com.GOBookingAPI.utils.DriverStatus;
import com.GOBookingAPI.utils.ManagerBooking;
import com.GOBookingAPI.utils.ManagerLocation;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.GOBookingAPI.payload.request.BookingCancelRequest;
import com.GOBookingAPI.payload.request.BookingRequest;

import com.GOBookingAPI.payload.response.BookingResponse;
import com.GOBookingAPI.services.IBookingService;

import java.util.Date;

@RestController
@RequestMapping("/bookings")
@Validated
public class BookingController {

    @Autowired
    private IBookingService bookingService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IWebSocketService webSocketService;

    @Autowired
    private ManagerLocation managerLocation;

    @Autowired
    private ManagerBooking managerBooking;

    @Autowired
    private IPaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> createBooking(@RequestBody @Valid BookingRequest bookingRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getByEmail(email);
        if (user != null) {
            return ResponseEntity.ok(bookingService.createBooking(user.getEmail(), bookingRequest));
        }
        throw new AccessDeniedException("User don't have permit to access");
    }

    @GetMapping("/travel-info")
    public ResponseEntity<?> getTravelInfo(@RequestParam @NotBlank String pickUpLocation, @RequestParam @NotBlank String dropOffLocation) {
        return ResponseEntity.ok(bookingService.getTravelInfo(pickUpLocation, dropOffLocation));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingByBookingId(@PathVariable int id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(bookingService.getBookingByBookingId(email, id));
    }

    @GetMapping
    public ResponseEntity<?> filter(@RequestParam(required = false) @Nullable   @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
                                    @RequestParam(required = false) @Nullable  @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
                                    @RequestParam(required = false) BookingStatus status,
                                    @RequestParam(required = false) String sortType,
                                    @RequestParam(required = false) String sortField,
                                    @RequestParam(required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                    @RequestParam(required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        //todo valid to and from
        return ResponseEntity.ok(bookingService.filterBookings(from, to, status, sortType,
                sortField, page, size, email));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> cancelBooking(@PathVariable int id, @RequestBody BookingCancelRequest cancelRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Booking booking = bookingService.cancelBookingForCustomer(email, id, cancelRequest);

        webSocketService.notifyBookingStatusToCustomer(booking.getCustomer().getId(), new BookingStatusResponse(booking.getId(), booking.getStatus()));
        if (booking.getDriver() != null) {
            webSocketService.notifyBookingStatusToCustomer(booking.getDriver().getId(), new BookingStatusResponse(booking.getId(), BookingStatus.CANCELLED));   //
            System.out.println("===> notify to driver: " + (new BookingStatusResponse(booking.getId(), booking.getStatus())).toString());
            if (booking.getStatus().equals(BookingStatus.WAITING_REFUND)) {
                System.out.println("Booking status : " + BookingStatus.WAITING_REFUND);
                managerBooking.deleteData(booking.getDriver().getId());
                managerLocation.updateDriverStatus(booking.getDriver().getId(), DriverStatus.FREE);
                paymentService.refundPayment(booking); // todo bug
            }
        }
        return ResponseEntity.ok(new BookingCancelResponse(booking.getId(), booking.getReasonType(), booking.getContentCancel(), booking.getStatus()));
    }

    @PutMapping("/{bookingId}/status")          //todo remove
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingResponse> changeBookingStatus(
            @PathVariable int bookingId,
            @RequestBody BookingStatusRequest cancelRequest) {      // todo validation
        BookingResponse response = bookingService.changeBookingStatusForAdmin(bookingId, cancelRequest.getBookingStatus());
        return ResponseEntity.ok(response);
    }


    @GetMapping("/active")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('DRIVER')")
    public ResponseEntity<?> getCurrentActiveBooking(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getByEmail(email);
        BookingResponse response = bookingService.getCurrentBooking(user);
        if(user.getFirstRole().getName().equals(RoleEnum.DRIVER) && user.getDriver().getStatus() == DriverStatus.FREE){
            //todo check have booking
//           webSocketService.notifytoDriver(user.getId(), "HAVEBOOKING");
        }
        return ResponseEntity.ok(response != null ? response : "null");
    }
    
    @GetMapping("/statisticsdate")
    public ResponseEntity<?> getStatisticsDay(@RequestParam(name ="from" , required = false) @Nullable @DateTimeFormat(pattern =  "yyyy-MM-dd") Date from,
    											@RequestParam(name = "to"  , required = false)@Nullable @DateTimeFormat(pattern =  "yyyy-MM-dd")  Date to,
    											@RequestParam(name ="statisticsType" , required = false) String statisticsType,
    											@RequestParam(name = "size" , required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
    											@RequestParam(name = "page" , required =  false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page
    											){
    	return ResponseEntity.ok(bookingService.getStatisticsBookingDate(from, to,  statisticsType,  size,  page));
    }
}
