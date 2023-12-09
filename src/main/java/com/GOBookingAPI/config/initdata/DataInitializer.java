package com.GOBookingAPI.config.initdata;

import com.GOBookingAPI.entities.Role;
import com.GOBookingAPI.entities.VehicleType;
import com.GOBookingAPI.enums.RoleEnum;
import com.GOBookingAPI.repositories.RoleRepository;
import com.GOBookingAPI.repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @EventListener(ContextRefreshedEvent.class)
    public void initData() {
//        this.initRole();
//        this.initVehicleType();
    }

    public void initRole(){
        Role cus = new Role();
        Role driver = new Role();
        Role admin = new Role();
//        cus.setName(RoleEnum.CUSTOMER.name());
//        driver.setName(RoleEnum.DRIVER.name());
//        admin.setName(RoleEnum.ADMIN.name());
        roleRepository.save(cus);
        roleRepository.save(driver);
        roleRepository.save(admin);
    }

    public void initVehicleType(){
        VehicleType moto = new VehicleType();
        VehicleType car = new VehicleType();
        moto.setName(com.GOBookingAPI.enums.VehicleType.MOTORCYCLE);
        car.setName(com.GOBookingAPI.enums.VehicleType.CAR);
        vehicleRepository.save(moto);
        vehicleRepository.save(car);
    }
}