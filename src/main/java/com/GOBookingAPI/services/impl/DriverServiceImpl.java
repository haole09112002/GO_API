package com.GOBookingAPI.services.impl;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.GOBookingAPI.enums.VehicleType;
import com.GOBookingAPI.payload.response.BookingStatusResponse;
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
}
