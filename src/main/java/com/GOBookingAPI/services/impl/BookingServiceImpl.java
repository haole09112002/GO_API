package com.GOBookingAPI.services.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.GOBookingAPI.entities.*;
import com.GOBookingAPI.enums.RoleEnum;
import com.GOBookingAPI.enums.VehicleType;
import com.GOBookingAPI.exceptions.AccessDeniedException;
import com.GOBookingAPI.exceptions.AppException;
import com.GOBookingAPI.exceptions.BadRequestException;
import com.GOBookingAPI.payload.response.*;
import com.GOBookingAPI.payload.vietmap.Path;
import com.GOBookingAPI.payload.vietmap.VietMapResponse;
import com.GOBookingAPI.services.IWebSocketService;
import com.GOBookingAPI.utils.AppUtils;
import com.GOBookingAPI.utils.DriverStatus;
import com.GOBookingAPI.utils.ManagerBooking;
import com.GOBookingAPI.utils.ManagerLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.request.BookingCancelRequest;
import com.GOBookingAPI.payload.request.BookingRequest;
import com.GOBookingAPI.repositories.BookingRepository;
import com.GOBookingAPI.repositories.CustomerRepository;
import com.GOBookingAPI.repositories.MyUserRepository;
import com.GOBookingAPI.services.IBookingService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BookingServiceImpl implements IBookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MyUserRepository myUserRepository;

    @Autowired
    private MapServiceImpl mapService;

    @Autowired
    private IWebSocketService webSocketService;

    @Autowired
    private ManagerLocation managerLocation;

    @Autowired
    private ManagerBooking managerBooking;

    @Override
    public BookingResponse createBooking(String username, BookingRequest req) {
        User user = myUserRepository.findByEmail(username).orElseThrow(() -> new NotFoundException("Không tìm thấy khách hàng"));
        Customer customer = customerRepository.findById(user.getId()).orElseThrow(() -> new NotFoundException("Không tìm thấy Customer"));
        System.out.println(user.toString());

        VietMapResponse vietMapResponse = mapService.getRoute(req.getPickUpLocation(), req.getDropOffLocation(), req.getVehicleType().name());
        if (vietMapResponse.getCode().equals("ERROR")) {
            throw new BadRequestException("pickUpLocation or dropOffLocation is invalid");
        }

        System.out.println("vietMapResponse.getFirstPath().getDistance(): " + vietMapResponse.getFirstPath().getDistance());

        if(vietMapResponse.getFirstPath().getDistance() <= 200)  //     quảng đường bé hơn 200m
            throw new BadRequestException("Quảng đường quá gần, chúng tôi chưa hổ trợ");

        if(vietMapResponse.getFirstPath().getDistance() >= 150000)  //     quảng đường lớn hơn 150km
            throw new BadRequestException("Quảng đường quá xa, chúng tôi chưa hổ trợ");

        long amount = this.calculatePrice(vietMapResponse.getFirstPath().getDistance(), req.getVehicleType());

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setDriver(null);
        booking.setStatus(BookingStatus.WAITING);
        booking.setPickupLocation(req.getPickUpLocation());
        booking.setDropoffLocation(req.getDropOffLocation());
        booking.setAmount(amount);
        booking.setVehicleType(req.getVehicleType());
        booking.setCreateAt(new Date());
        bookingRepository.save(booking);

        BookingResponse resp = new BookingResponse();
        resp.setId(booking.getId());
        resp.setDriverId(booking.getDriver() != null ? booking.getDriver().getId() : null);
        resp.setCreateAt(booking.getCreateAt());
        resp.setPaymentMethod(booking.getPayment() != null ? booking.getPayment().getPaymentMethod() : null);
        resp.setCustomerId(booking.getCustomer().getId());
        resp.setAmount(booking.getAmount());
        resp.setDropOffLocation(booking.getDropoffLocation());
        resp.setPickupLocation(booking.getPickupLocation());
        resp.setStatus(booking.getStatus());
        resp.setVehicleType(booking.getVehicleType());
        resp.setDistance(vietMapResponse.getFirstPath().getDistance());
        resp.setPredictTime(vietMapResponse.getFirstPath().getTime());
        return resp;
    }

    @Override
    public void changeBookingStatus(String username, int bookingId, BookingStatus targetStatus) {
        User user = myUserRepository.findByEmail(username).orElseThrow(() -> new NotFoundException("Không tìm thấy khách hàng"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Không tìm thấy booking id: " + bookingId));
        if (!booking.getCustomer().getUser().getEmail().equals(username)) {
            throw new BadRequestException("Booking id " + booking + " không thuộc về khách hàng " + username);
        }
        booking.setStatus(targetStatus);
        bookingRepository.save(booking);
    }

    @Override
    public TravelInfoResponse getTravelInfo(String pickUpLocation, String dropOffLocation) {
        Map<Integer, Long> amounts = new HashMap<>();
        for (VehicleType type : VehicleType.values()) {                    //todo save database vehicle type
            VietMapResponse travel = mapService.getRoute(pickUpLocation, dropOffLocation, type.name());
            if (travel.getCode().equals("ERROR")) {
                throw new BadRequestException("pickUpLocation or dropOffLocation is invalid");
            }
            Path path = travel.getPaths().get(0);
            long total = this.calculatePrice(path.getDistance(), type);
            amounts.put(type.getValue(), total);
        }
        return new TravelInfoResponse(pickUpLocation, dropOffLocation, amounts);
    }

    @Override
    public BookingResponse getBookingByBookingId(String email, int bookingId) {
        User user = myUserRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Khong tim thay booking"));
        if (user.getRoles().stream().anyMatch(role -> (RoleEnum.DRIVER.equals(role.getName())))) {
            if (booking.getDriver() == null) {
                throw new NotFoundException("Không tìm thấy booking");
            } else if (booking.getDriver().getUser().getId() != user.getId()) {
                throw new NotFoundException("Không tìm thấy booking");
            }
        }
        if (user.getRoles().stream().anyMatch(role -> (RoleEnum.CUSTOMER.equals(role.getName())))) {
            if (booking.getCustomer().getUser().getId() != user.getId()) {
                throw new NotFoundException("Không tìm thấy booking");
            }
        }
        BookingResponse resp = new BookingResponse();
        resp.setId(booking.getId());
        resp.setDriverId(booking.getDriver() != null ? booking.getDriver().getId() : null);
        resp.setCreateAt(booking.getCreateAt());
        resp.setPaymentMethod(booking.getPayment() != null ? booking.getPayment().getPaymentMethod() : null);
        resp.setCustomerId(booking.getCustomer().getId());
        resp.setAmount(booking.getAmount());
        resp.setDropOffLocation(booking.getDropoffLocation());
        resp.setPickupLocation(booking.getPickupLocation());
        resp.setStatus(booking.getStatus());
        resp.setVehicleType(booking.getVehicleType());
        return resp;
    }

    @Override
    public PagedResponse<BookingResponse> getListBookingByUser(String email, Date from, Date to, int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);
        PageRequest pageable = PageRequest.of(page, size);
        User user = myUserRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
        Page<Booking> bookingPage = null;
        Iterator<Role> iterator = user.getRoles().iterator();
        if (iterator.hasNext()) {
            switch (iterator.next().getName()) {
                case ADMIN:
                    bookingPage = bookingRepository.findBookingBetweenAndCustomer(from, to, user.getId(), pageable);
                    break;
                case DRIVER:
                    bookingPage = bookingRepository.findBookingBetweenAndDriver(from, to, user.getId(), pageable);
                    break;
                case CUSTOMER:
                    bookingPage = bookingRepository.findBookingBetween(from, to, pageable);
                    break;
            }
        } else {
            throw new AppException("Có lỗi xảy ra");
        }
        List<BookingResponse> bookingResponses = new ArrayList<>(bookingPage.getContent().size());
        bookingResponses = bookingPage.getContent().stream().map(booking -> {
            BookingResponse resp = new BookingResponse();
            resp.setId(booking.getId());
            resp.setDriverId(booking.getDriver() != null ? booking.getDriver().getId() : null);
            resp.setCreateAt(booking.getCreateAt());
            resp.setPaymentMethod(booking.getPayment() != null ? booking.getPayment().getPaymentMethod() : null);
            resp.setCustomerId(booking.getCustomer().getId());
            resp.setAmount(booking.getAmount());
            resp.setDropOffLocation(booking.getDropoffLocation());
            resp.setPickupLocation(booking.getPickupLocation());
            resp.setStatus(booking.getStatus());
            resp.setVehicleType(booking.getVehicleType());
            return resp;
        }).collect(Collectors.toList());
        return new PagedResponse<>(bookingResponses, bookingPage.getNumber(), bookingPage.getSize(),
                bookingPage.getTotalElements(), bookingPage.getTotalPages(), bookingPage.isLast());
    }

    @Override           // use for ADMIN
    public BookingResponse changeBookingStatusForAdmin(int bookingId, BookingStatus status) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Khong tim thay booking"));
        BookingResponse resp = new BookingResponse();
        resp.setId(booking.getId());
        resp.setDriverId(booking.getDriver() != null ? booking.getDriver().getId() : null);
        resp.setCreateAt(booking.getCreateAt());
        resp.setPaymentMethod(booking.getPayment() != null ? booking.getPayment().getPaymentMethod() : null);
        resp.setCustomerId(booking.getCustomer().getId());
        resp.setAmount(booking.getAmount());
        resp.setDropOffLocation(booking.getDropoffLocation());
        resp.setPickupLocation(booking.getPickupLocation());
        resp.setStatus(booking.getStatus());
        resp.setVehicleType(booking.getVehicleType());
        return resp;
    }

//    @Override
//    public BookingStatusResponse changeBookingStatusAndNotify(String email, BookingStatusRequest req) {
//        User user = myUserRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
//        Booking booking = bookingRepository.findById(req.getBookingId()).orElseThrow(() -> new NotFoundException("Khong tim thay booking"));
//        switch (req.getBookingStatus()) {
//            case CANCELLED:
//                if (booking.getStatus() == BookingStatus.CANCELLED)
//                    throw new BadRequestException("Đơn đặt đã bị hủy trước đó");
//                if (booking.getStatus() == BookingStatus.ON_RIDE)
//                    throw new BadRequestException("Bạn đang di chuyển trên xe không thể hủy");
//                if (booking.getStatus() == BookingStatus.REFUNDED)
//                    throw new BadRequestException("Đơn đặt đã được hoàn tiền, không thể hủy");
////				if(booking.getStatus() == BookingStatus.WAITING || booking.getStatus() == BookingStatus.PAID)
//
//                break;
//            case PAID:
//                if (booking.getStatus() != BookingStatus.WAITING) {
//                    throw new BadRequestException("Bạn không thể thay đổi trạng thái");
//                }
//                break;
//
//            case REFUNDED:
//                if (booking.getStatus() != BookingStatus.CANCELLED) {
//                    throw new BadRequestException("Bạn không thể thay đổi trạng thái");
//                }
//                break;
//            case COMPLETE:
//                if (booking.getStatus() != BookingStatus.ON_RIDE) {
//                    throw new BadRequestException("Bạn không thể thay đổi trạng thái");
//                }
//                break;
//            case ON_RIDE:
//                if (booking.getStatus() != BookingStatus.PAID) {
//                    throw new BadRequestException("Bạn không thể thay đổi trạng thái");
//                }
//            default:
//                throw new BadRequestException("Booking status không tồn tại");
//        }
//        booking.setStatus(req.getBookingStatus());
//        bookingRepository.save(booking);
//        BookingStatusResponse resp = new BookingStatusResponse(booking.getId(), booking.getStatus());
//        return resp;
////        messagingTemplate.convertAndSendToUser(String.valueOf(user.getId()), "/bookings/status", req);
//    }

    @Override
    public BookingStatusResponse cancelBookingForCustomer(String email, int bookingId, BookingCancelRequest req) {
        User user = myUserRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
        Booking booking = bookingRepository.findById(req.getBookingId()).orElseThrow(() -> new NotFoundException("Khong tim thay booking"));

        if (user.getId() !=(booking.getCustomer().getUser().getId())) {
            throw new AccessDeniedException("Booking này không thuộc về bạn");
        }

        BookingStatus currentStatus = booking.getStatus();
        BookingStatus requestedStatus = BookingStatus.CANCELLED;

        if (currentStatus == BookingStatus.CANCELLED) {
            throw new BadRequestException("Đơn đặt đã bị hủy trước đó");
        }

        switch (currentStatus) {
            case ON_RIDE:
                throw new BadRequestException("Bạn đang di chuyển trên xe không thể hủy");
            case REFUNDED:
                throw new BadRequestException("Đơn đặt đã được hoàn tiền, không thể hủy");
            case WAITING_REFUND:
                throw new BadRequestException("Đơn đặt đang chờ hoàn tiền, không thể hủy");
            case WAITING:
                // todo kiem tra da qua thoi gian chua
                if (requestedStatus != BookingStatus.CANCELLED) {
                    throw new BadRequestException("Bạn chỉ có thể hủy đơn");
                }
                break;
            case PAID:
                if (requestedStatus != BookingStatus.WAITING_REFUND) {
                    throw new BadRequestException("Bạn chỉ có thể chuyển sang trạng thái chờ hoàn tiền");
                }
                break;
        }

        booking.setStatus(requestedStatus);
        bookingRepository.save(booking);

        // Trả về thông tin trạng thái mới của đơn đặt
        return new BookingStatusResponse(booking.getId(), booking.getStatus());
    }


    @Override
    public BaseResponse<Booking> Confirm(int id) {
        try {
//			Booking booking = bookingRepository.findById(id);
//			if(booking != null) {
//				Date curentlydate = new Date();
//				booking.setStartTime(curentlydate);
//				bookingRepository.save(booking);
//				return new BaseResponse<Booking>( booking ,"Confirm Success");
//			}else {
//				return new BaseResponse<Booking>( null, "Confirm fail!");
//			}
            return null;
        } catch (Exception e) {
            log.info("error in Booking Service");
            return null;
        }
    }

    @Override
    public BaseResponse<Booking> Cancel(BookingCancelRequest req) {
        try {
//			Booking booking = bookingRepository.findById(req.getBookingId());
//			if(booking != null) {
//			booking.setReasonType(req.getReasonType());
//			booking.setContentCancel(req.getContent());
//			bookingRepository.save(booking);
//			return new BaseResponse<Booking>(null, "Cancel Success");
//			}else {
//				return new BaseResponse<Booking>( null, "Cancel fail!");
//			}
            return null;
        } catch (Exception e) {
            log.info("error in Booking Service");
            return null;
        }
    }

    public long calculatePrice(double distance, VehicleType vehicleType) {
        double total = (distance / 1000.0) * vehicleType.getPrice();
        return Math.round(total);
    }

    @Override
    public boolean isDriverBelongsToCustomerBooking(User cus, int driverId){
       List<Booking> bookingList = bookingRepository.findByCustomerId(cus.getId(), driverId);
       return  bookingList.size() > 0;
    }

    public void changeBookingStatusAndNotify(String email, int bookingId, BookingStatus bookingStatus){
        User user = myUserRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Khong tim thay booking"));

        if(user.getFirstRole().getName().equals(RoleEnum.CUSTOMER) && bookingStatus.equals(BookingStatus.CANCELLED)){

            if(booking.getStatus() != BookingStatus.WAITING || booking.getStatus() != BookingStatus.PAID || booking.getStatus() != BookingStatus.FOUND){
                System.out.println("==> booking.getStatus() != BookingStatus.WAITING || booking.getStatus() != BookingStatus.PAID");
                return;
            }

            if(booking.getStatus().equals(BookingStatus.PAID))
            {
                booking.setStatus(BookingStatus.WAITING_REFUND);
            }else {
                booking.setStatus(BookingStatus.CANCELLED);
            }
        }

        if(user.getFirstRole().getName().equals(RoleEnum.DRIVER)){
            if(!bookingStatus.equals(BookingStatus.ON_RIDE) && bookingStatus.equals(BookingStatus.COMPLETE)){
                System.out.println("==> !bookingStatus.equals(BookingStatus.ON_RIDE) && bookingStatus.equals(BookingStatus.COMPLETE)");
                return; //todo exception socket handler
            }

            if(!booking.getStatus().equals(BookingStatus.FOUND) && bookingStatus.equals(BookingStatus.ON_RIDE))
            {
                System.out.println("==>Trạng thái thay đổi không hợp lệ không thể từ : " + booking.getStatus() +"=> " + bookingStatus);
                return;
            }

            if(!booking.getStatus().equals(BookingStatus.ON_RIDE) && bookingStatus.equals(BookingStatus.COMPLETE))
            {
                System.out.println("==>Trạng thái thay đổi không hợp lệ không thể từ : " + booking.getStatus() +"=> " + bookingStatus);
                return;
            }
            booking.setStatus(bookingStatus);
        }

        if(user.getFirstRole().getName().equals(RoleEnum.ADMIN)){
            //todo check
            booking.setStatus(bookingStatus);
        }

        bookingRepository.save( booking);
        webSocketService.notifyBookingStatusToCustomer(booking.getCustomer().getId(), new BookingStatusResponse(booking.getId(), booking.getStatus()));
        if (booking.getDriver() != null){
            webSocketService.notifyBookingStatusToCustomer(booking.getDriver().getId(), new BookingStatusResponse(booking.getId(), booking.getStatus()));   //
            managerBooking.deleteData(booking.getDriver().getId());
            managerLocation.updateDriverStatus(booking.getDriver().getId(), DriverStatus.FREE);
        }
    }

    private void processChangeBookingForDriver(User user, BookingStatus newStatus, int bookingId){
        Driver driver = user.getDriver();
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()-> new NotFoundException("Không tìm thấy booking, id: " + bookingId));

        if(!newStatus.equals(BookingStatus.ON_RIDE) && !newStatus.equals(BookingStatus.COMPLETE))
        {
            System.out.println("==> FAIL, booking status is not permit: " + newStatus);
            return;
        }

//        if()
        //todo
    }
}
