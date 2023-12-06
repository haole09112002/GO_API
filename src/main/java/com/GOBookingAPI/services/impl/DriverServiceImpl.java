package com.GOBookingAPI.services.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.GOBookingAPI.enums.VehicleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.enums.BookingStatus;

import com.GOBookingAPI.enums.WebSocketBookingTitle;
import com.GOBookingAPI.exceptions.BadRequestException;
import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.vietmap.Path;
import com.GOBookingAPI.payload.vietmap.VietMapResponse;
import com.GOBookingAPI.repositories.BookingRepository;
import com.GOBookingAPI.repositories.DriverRepository;
import com.GOBookingAPI.services.IConservationService;
import com.GOBookingAPI.services.IDriverService;
import com.GOBookingAPI.services.IWebSocketService;
import com.GOBookingAPI.utils.DriverStatus;
import com.GOBookingAPI.utils.LocationDriver;
import com.GOBookingAPI.utils.ManagerBooking;
import com.GOBookingAPI.utils.ManagerLocation;

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
    private IConservationService conservationService;
    @Autowired
    private ManagerBooking managerBooking;

//    @Autowired
//    private IWebSocketService webSocketService;
//
//    @Autowired
//    public DriverServiceImpl(IWebSocketService webSocketService) {
//        this.webSocketService = webSocketService;
//    }

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
        Driver driverChosen = driverRepository.findById(id_driver).orElseThrow(() -> new NotFoundException("Khong tim thay Driver"));
        return driverChosen;
    }

    private static final int WAITING_TIME_SECONDS = 5; // Thời gian chờ sau

    @Override
    public void scheduleFindDriverTask(Booking booking, String locationCustomer) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> findAndNotifyDriver(booking, locationCustomer), WAITING_TIME_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    public void findAndNotifyDriver(Booking booking, String locationCustomer) {
        Driver driverChosen = findDriverBooking(locationCustomer, booking.getVehicleType());
        driverChosen.setStatus(DriverStatus.ON_RIDE);
        driverRepository.save(driverChosen);

        booking.setDriver(driverChosen);
        booking.setStatus(BookingStatus.FOUND);
        bookingRepository.save(booking);

        conservationService.createConservation(booking);

        managerBooking.AddData(driverChosen.getId(), booking.getCustomer().getId());
        managerLocation.updateDriverStatus(driverChosen.getId(), driverChosen.getStatus());

//         gui thong tin tai xe ve khach
//        webSocketService.notifyDriverToCustomer(booking.getCustomer().getId(), driverChosen.getId());
//         gui thong tin booking ve tai xe
//        webSocketService.notifyBookingToDriver(driverChosen.getId(), booking.getId());
    }

    @Override
    public List<Driver> getDriverByStatus(DriverStatus status) {
        // TODO Auto-generated method stub
        return driverRepository.findDriverStatus(status);
    }
}
