package com.GOBookingAPI.services.impl;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.enums.VehicleType;
import com.GOBookingAPI.exceptions.AccessDeniedException;
import com.GOBookingAPI.exceptions.BadCredentialsException;
import com.GOBookingAPI.payload.response.BookingStatusResponse;
import com.GOBookingAPI.payload.response.DriverBaseInfoResponse;
import com.GOBookingAPI.payload.response.DriverInfoResponse;
import com.GOBookingAPI.payload.response.DriverStatusResponse;
import com.GOBookingAPI.repositories.UserRepository;
import com.GOBookingAPI.services.IBookingService;
import com.GOBookingAPI.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.enums.BookingStatus;

import com.GOBookingAPI.exceptions.BadRequestException;
import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.vietmap.Path;
import com.GOBookingAPI.payload.vietmap.VietMapResponse;
import com.GOBookingAPI.repositories.BookingRepository;
import com.GOBookingAPI.repositories.DriverRepository;
import com.GOBookingAPI.services.ConversationService;
import com.GOBookingAPI.services.IDriverService;
import com.GOBookingAPI.services.IWebSocketService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DriverServiceImpl implements IDriverService {
    @Autowired
    private MapServiceImpl mapService;

    @Autowired
    private ManagerLocation managerLocation;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ConversationService conservationService;

    @Autowired
    private ManagerBooking managerBooking;

    @Autowired
    private IWebSocketService webSocketService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IBookingService bookingService;

    @Override
    public Driver findDriverBooking(String locationCustomer, VehicleType vehicleType) {
        int id_driver = 0;
        double minDistance = 1000000;
        for (LocationDriver driver : managerLocation.getLocationMapFree().values()) {
            if (!driver.getVehicleType().equals(vehicleType)) {
                break;
            }

            VietMapResponse travel = mapService.getRoute(locationCustomer, driver.getLocation(), vehicleType.name());
            if (travel.getCode().equals("ERROR")) {
                System.out.println("==>pickUpLocation or dropOffLocation is invalid");
                throw new BadRequestException("pickUpLocation or dropOffLocation is invalid");
            }

            Path path = travel.getFirstPath();
            if (path.getDistance() < minDistance) {
                minDistance = path.getDistance();
                id_driver = driver.getDriverId();
            }
        }
        System.out.println("==> founded driver " + id_driver);
        return driverRepository.findById(id_driver).orElse(null);
    }

    @Override
    public void scheduleFindDriverTask(Booking booking, String locationCustomer) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            Booking updateBooking = bookingRepository.findById(booking.getId()).orElseThrow(() -> new NotFoundException("khong tim thay booking"));
            if (updateBooking.getStatus().equals(BookingStatus.CANCELLED) || updateBooking.getStatus().equals(BookingStatus.WAITING_REFUND)) {
                executorService.shutdown();
                return;
            }

            boolean driverFound = findAndNotifyDriver(updateBooking, locationCustomer);

            if (driverFound) {
                executorService.shutdown();
                return;
            }

            if (AppUtils.currentTimeInSecond() - updateBooking.getCreateAt().getTime() / 1000 > AppConstants.MAX_TIME_PENDING) {
                updateBooking.setStatus(BookingStatus.WAITING_REFUND);
                bookingRepository.save(updateBooking);
                webSocketService.notifyBookingStatusToCustomer(updateBooking.getCustomer().getId(), new BookingStatusResponse(updateBooking.getId(), updateBooking.getStatus()));
                executorService.shutdown();
            }
        }, AppConstants.INIT_DELAY, AppConstants.PERIOD_TIME, TimeUnit.SECONDS);
    }

    @Override
    @Transactional
    public boolean findAndNotifyDriver(Booking booking, String locationCustomer) {
        Driver driverChosen = findDriverBooking(locationCustomer, booking.getVehicleType());

        if (driverChosen == null)
            return false;

        driverChosen.setStatus(DriverStatus.ON_RIDE);
        driverRepository.save(driverChosen);

        booking.setDriver(driverChosen);
        booking.setStatus(BookingStatus.FOUND);
        bookingRepository.save(booking);

        conservationService.createConservation(booking);

        managerBooking.AddData(driverChosen.getId(), booking.getCustomer().getId());
        managerLocation.updateDriverStatus(driverChosen.getId(), driverChosen.getStatus());

        webSocketService.notifyDriverToCustomer(booking.getCustomer().getId(), driverChosen.getId());
        webSocketService.notifyBookingToDriver(driverChosen.getId(), booking.getId());
        return true;
    }

    @Override
    public List<Driver> getDriverByStatus(DriverStatus status) {
        // TODO Auto-generated method stub
        return driverRepository.findDriverStatus(status);
    }

    @Override
    public DriverInfoResponse getDriverInfo(String email, Integer driverId) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Không tìm thấy user , email: " + email));
        boolean isAllow = false;
        switch (user.getFirstRole().getName()) {
            case CUSTOMER -> {
                if (driverId == -1)
                    throw new BadRequestException("Thieu driverId");
                if (bookingService.isDriverBelongsToCustomerBooking(user, driverId))
                    isAllow = true;
                else
                    throw new AccessDeniedException("Bạn chưa từng có chuyến đi với tài xế này");
            }
            case DRIVER -> {
                driverId = user.getId();
                isAllow = true;
            }
            case ADMIN -> {
                if (driverId == -1)
                    throw new BadRequestException("Thieu driverId");
                isAllow = true;
            }
        }
        System.out.println("====> driverId" + driverId);
        if (isAllow) {
            DriverInfoResponse resp = new DriverInfoResponse();

            Driver driver = driverRepository.findById(driverId).orElseThrow(() -> new NotFoundException("Không tìm thấy driver , driver: " + email));
            resp.setDriverInfoUrl(driver.getImgUrl());
            resp.setId(driver.getId());
            resp.setEmail(driver.getUser().getEmail());
            resp.setFullName(driver.getFullName());
            resp.setMale(driver.isGender());
            resp.setDateOfBirth(driver.getDateOfBirth());
            resp.setPhoneNumber(driver.getUser().getPhoneNumber());
            resp.setStatus(driver.getStatus());
            resp.setRating(driver.getRating());
            resp.setNonBlock(driver.getUser().getIsNonBlock());
            resp.setAvtUrl(driver.getUser().getAvatarUrl());
            resp.setLicensePlate(driver.getLicensePlate());
            resp.setDrivingLicense(driver.getDrivingLicense());
            resp.setIdCard(driver.getIdCard());
            resp.setVehicleType(driver.getFirstVehicleType().getName());
            return resp;
        }
        throw new AccessDeniedException("You don't have permission to access this resource");

    }

    @Override
    public DriverBaseInfoResponse getDriverBaseInfo(String email, Integer driverId) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Không tìm thấy user , email: " + email));
        if (bookingService.isDriverBelongsToCustomerBooking(user, driverId)){
            Driver driver = driverRepository.findById(driverId).orElseThrow(() -> new NotFoundException("Không tìm thấy driver , driverId: " + driverId));

            DriverBaseInfoResponse resp = new DriverBaseInfoResponse();
            resp.setId(driver.getId());
            resp.setEmail(user.getEmail());
            resp.setFullName(driver.getFullName());
            resp.setMale(driver.isGender());
            resp.setDateOfBirth(driver.getDateOfBirth());
            resp.setPhoneNumber(driver.getUser().getPhoneNumber());
            resp.setRating(driver.getRating());
            resp.setNonBlock(driver.getUser().getIsNonBlock());
            resp.setAvtUrl(driver.getUser().getAvatarUrl());
            resp.setLicensePlate(driver.getLicensePlate());
            resp.setVehicleType(driver.getFirstVehicleType().getName());
            return resp;
        }
        throw new AccessDeniedException("Bạn chưa từng có chuyến đi với tài xế này");
    }

    @Override
    public DriverStatusResponse changeDriverStatus(int driverId, DriverStatus newStatus) {
        Driver driver = driverRepository.findById(driverId).orElseThrow(()-> new NotFoundException("Khong tim thay driver, driverId: " + driverId));
        driver.setStatus(newStatus);
        driverRepository.save(driver);
        return new DriverStatusResponse(driverId, newStatus);
    }

    @Override
    public Driver getById(int id) {
        return driverRepository.findById(id).orElseThrow(()-> new NotFoundException("Khong tim thay driver, driverId: " + id));
    }
}
