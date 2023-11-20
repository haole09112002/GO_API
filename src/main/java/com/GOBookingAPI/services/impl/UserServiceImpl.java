package com.GOBookingAPI.services.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import java.util.Optional;

import com.GOBookingAPI.exceptions.BadRequestException;
import com.google.api.gax.rpc.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.GOBookingAPI.entities.Customer;
import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.entities.Role;
import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.entities.VehicleType;
import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.request.CustomerRequest;
import com.GOBookingAPI.payload.request.DriverRequest;
import com.GOBookingAPI.payload.response.BaseResponse;
import com.GOBookingAPI.payload.response.LoginResponse;
import com.GOBookingAPI.payload.response.RegisterResponse;
import com.GOBookingAPI.repositories.CustomerRepository;
import com.GOBookingAPI.repositories.DriverRepository;
import com.GOBookingAPI.repositories.RoleRepository;
import com.GOBookingAPI.repositories.UserRepository;
import com.GOBookingAPI.repositories.VehicleRepository;
import com.GOBookingAPI.services.IUserService;
import com.google.api.client.util.Base64;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Override
    public BaseResponse<LoginResponse> loadUserbyEmail(String email) {
        try {
            Optional<User> userOptional = userRepository.findByEmail(email);

            if (!userOptional.isPresent()) {
                return new BaseResponse<LoginResponse>(new LoginResponse("unregistered", null), "User not found");
            } else {
                User user = userOptional.get();
                String roleName = "";
                for (Role role : user.getRoles()) {
                    roleName = role.getName();
                    break;
                }
                if (user.getIsNonBlock()) {
                    return new BaseResponse<LoginResponse>(new LoginResponse("blocked", roleName), "User is blocked");
                } else {
                    if (roleName.equals("DRIVER")) {
                        Optional<Driver> driverOptional = driverRepository.findById(user.getId());
                        Driver driver = driverOptional.get();
                        if (driver.getStatus().equals("NOACTIVE")) {
                            return new BaseResponse<LoginResponse>(new LoginResponse("uncheck", roleName), "Driver uncheck");
                        }
                    }
                    return new BaseResponse<LoginResponse>(new LoginResponse("registered", roleName), "User registered");

                }
            }

        } catch (Exception e) {
            log.info("Error in UserService");
            return new BaseResponse<LoginResponse>(null, e.getMessage());
        }
    }

    @Override

    public User getByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
        return user;
    }

    @Override
    public RegisterResponse registerCustomer(CustomerRequest customerRequest, MultipartFile avatar) {
        User user = new User();
        Date currentDate = new Date();
        user.setEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        user.setPhoneNumber(customerRequest.getPhoneNumber());
        user.setIsNonBlock(false);
        user.setCreateDate(currentDate);
        Set<Role> roles = new HashSet<>();
        Role role = roleRepository.findByName("CUSTOMER").orElseThrow(() -> new NotFoundException("Khong tim thay role"));
        roles.add(role);
        user.setRoles(roles);
        if (avatar != null) {
            try {
                byte[] avatarBytes = avatar.getBytes();
                String avatarString = Base64.encodeBase64String(avatarBytes);
                user.setAvatarUrl(avatarString);
            }catch (Exception e){
                throw new BadRequestException("Khong the chuyen doi anh");
            }

        }
        userRepository.save(user);
        User usersaved = userRepository.findByEmail(user.getEmail()).orElseThrow(() -> new NotFoundException("Khong tim thay user voi email : " + user.getEmail()));

        Customer newcustomer = new Customer();
        if (customerRequest.getDateOfBirth() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dateofBirth = null;
            try {
                dateofBirth = dateFormat.parse(customerRequest.getDateOfBirth());
                newcustomer.setDateOfBirth(dateofBirth);
            }catch (Exception e){
                throw new BadRequestException("Sai định dạng yyyy-MM-dd");
            }
        }
        newcustomer.setId(usersaved.getId());
        newcustomer.setFullName(customerRequest.getFullName());
        newcustomer.setGender(customerRequest.getGender());
        newcustomer.setUser(usersaved);
        customerRepository.save(newcustomer);
        return new RegisterResponse("Success");
    }

    @Override
    public RegisterResponse registerDriver(DriverRequest driverRequest) {
        try {
            User user = new User();
            Date currentDate = new Date();
            user.setEmail(SecurityContextHolder.getContext().getAuthentication().getName());
            user.setPhoneNumber(driverRequest.getPhoneNumber());
            user.setIsNonBlock(false);
            user.setCreateDate(currentDate);
            Set<Role> roles = new HashSet<>();
            Optional<Role> roleOptional = roleRepository.findByName("DRIVER");
            roles.add(roleOptional.get());
            user.setRoles(roles);
            if (driverRequest.getAvatar() != null) {
                byte[] avatarBytes = driverRequest.getAvatar().getBytes();
                String avatarString = Base64.encodeBase64String(avatarBytes);
                user.setAvatarUrl(avatarString);
            }

            userRepository.save(user);
            Optional<User> userOptional = userRepository.findByEmail(user.getEmail());
            User usersaved = userOptional.get();
            Driver newdriver = new Driver();
            if (driverRequest.getDateOfBirth() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date dateofBirth = dateFormat.parse(driverRequest.getDateOfBirth());
                newdriver.setDateOfBirth(dateofBirth);
            }
            if (driverRequest.getLicensePlate() != null) {
                byte[] licensePlateBytes = driverRequest.getLicensePlate().getBytes();
                String licensePlateString = Base64.encodeBase64String(licensePlateBytes);
                newdriver.setLicensePlate(licensePlateString);
            }
            newdriver.setId(usersaved.getId());
            newdriver.setFullName(driverRequest.getFullName());
            newdriver.setGender(driverRequest.getGender());
            newdriver.setIdCard(driverRequest.getIdCard());

            Set<VehicleType> vehicles = new HashSet<>();
            Optional<VehicleType> vehicleOptional = vehicleRepository.findByName(driverRequest.getVehicle());
            vehicles.add(vehicleOptional.get());
            newdriver.setVehicles(vehicles);
            newdriver.setStatus("NOACTIVE");
            newdriver.setUser(usersaved);
            driverRepository.save(newdriver);
            return new RegisterResponse("Success");
        } catch (Exception e) {
            log.info("Error Register Service! {}", e.getMessage());
            return new RegisterResponse("Fail");
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                return userOptional;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.info("Error in UserService");
            return null;
        }
    }


}
