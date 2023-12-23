package com.GOBookingAPI.services.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


import com.GOBookingAPI.enums.DriverInfoImg;
import com.GOBookingAPI.enums.RoleEnum;
import com.GOBookingAPI.exceptions.AppException;
import com.GOBookingAPI.exceptions.BadCredentialsException;
import com.GOBookingAPI.exceptions.BadRequestException;
import com.GOBookingAPI.payload.request.DriverRegisterRequest;
import com.GOBookingAPI.payload.response.*;
import com.GOBookingAPI.repositories.*;
import com.GOBookingAPI.utils.DriverStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.GOBookingAPI.entities.Customer;
import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.entities.Role;
import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.entities.VehicleType;
import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.request.CustomerRequest;
import com.GOBookingAPI.services.IUserService;

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

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public BaseResponse<LoginResponse> loadUserbyEmail(String email) {

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return new BaseResponse<LoginResponse>(new LoginResponse("unregistered", null, -1), "User not found");
        }

        User user = userOptional.get();

        if (!user.getIsNonBlock()) {
            return new BaseResponse<LoginResponse>(new LoginResponse("blocked", user.getFirstRole().getName(), user.getId()), "User is blocked");
        }

        if (user.getFirstRole().getName().equals(RoleEnum.DRIVER)) {
            Driver driver = driverRepository.findById(user.getId()).orElseThrow(() -> new AppException("Server error"));
            if (driver.getStatus().equals(DriverStatus.NOT_ACTIVATED)) {
                return new BaseResponse<LoginResponse>(new LoginResponse("uncheck", user.getFirstRole().getName(), user.getId()), "Driver uncheck");
            }
        }
        return new BaseResponse<LoginResponse>(new LoginResponse("registered", user.getFirstRole().getName(), user.getId()), "User registered");
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
    }

    @Override
    public User getById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
    }

    @Override
    @Transactional
    public RegisterCustomerResponse registerCustomer(MultipartFile avatar, String phoneNumber, String fullName, boolean isMale, String dateOfBirth) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            throw new BadRequestException("User has already been registered");
        }
        User user = userOptional.orElseGet(() -> registerUser(email, phoneNumber, avatar, RoleEnum.CUSTOMER));
        userRepository.save(user);
        Customer newCustomer = new Customer();
        newCustomer.setUser(user);
        Date date = null;
        if (dateOfBirth != null) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                date = dateFormat.parse(dateOfBirth);
                newCustomer.setDateOfBirth(date);
            } catch (Exception e) {
                newCustomer.setDateOfBirth(null);
            }
        } else
            newCustomer.setDateOfBirth(null);
        newCustomer.setId(user.getId());
        newCustomer.setFullName(fullName);
        newCustomer.setGender(isMale);
        customerRepository.save(newCustomer);
        return new RegisterCustomerResponse(newCustomer.getId(), newCustomer.getFullName(), email, user.getIsNonBlock(), user.getPhoneNumber(), newCustomer.getDateOfBirth(), newCustomer.getGender(), user.getAvatarUrl());
    }

    @Override
    @Transactional
    public DriverInfoResponse registerDriver(DriverRegisterRequest req) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            throw new BadRequestException("User has already been registered");
        }
        //todo phone number da ton ai
        User user = userOptional.orElseGet(() -> registerUser(email, req.getPhoneNumber(), req.getAvatar(), RoleEnum.DRIVER));
        userRepository.save(user);
        Driver driver = new Driver();
        Date date = null;
        driver.setUser(user);

        if (req.getDateOfBirth() != null) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                date = dateFormat.parse(req.getDateOfBirth());
                driver.setDateOfBirth(date);
            } catch (Exception e) {
                throw new BadRequestException("Ngay sinh khong hop le");
            }
        } else
            throw new BadRequestException("Ngay sinh khong duong rong ");

        driver.setId(user.getId());
        driver.setFullName(req.getFullName());
        driver.setGender(req.isMale());
        driver.setUser(user);
        driver.setIdCard(req.getIdCard());
        driver.setStatus(DriverStatus.NOT_ACTIVATED);
        driver.setLicensePlate(req.getLicensePlate());
        driver.setDrivingLicense(req.getDrivingLicense());

        String fileName = "";
        for (int i = 0; i < req.getDrivingLicenseImg().length; i++) {
            fileName = fileStorageService.createRootImgUrl(req.getDrivingLicenseImg()[i], DriverInfoImg.DrivingLicense, req.getPhoneNumber(), i);
        }
        for (int i = 0; i < req.getIdCardImg().length; i++) {
            fileStorageService.createRootImgUrl(req.getIdCardImg()[i], DriverInfoImg.IdCard, req.getPhoneNumber(), i + 2);
        }

        Set<VehicleType> vehicleTypes = new HashSet<>();
        VehicleType type = vehicleRepository.findByName(req.getVehicleType()).orElseThrow(() -> new NotFoundException("Loai xe khong hop le"));
        vehicleTypes.add(type);
        driver.setImgUrl(fileName);
        driver.setVehicles(vehicleTypes);
        driver.setUser(user);
        driverRepository.save(driver);
        DriverInfoResponse resp = new DriverInfoResponse();
        resp.setDriverInfoUrl(fileName);
        resp.setId(driver.getId());
        resp.setEmail(user.getEmail());
        resp.setFullName(driver.getFullName());
        resp.setMale(driver.isGender());
        resp.setDateOfBirth(driver.getDateOfBirth());
        resp.setPhoneNumber(user.getPhoneNumber());
        resp.setStatus(driver.getStatus());
        resp.setRating(driver.getRating());
        resp.setNonBlock(user.getIsNonBlock());
        resp.setAvtUrl(user.getAvatarUrl());
        resp.setLicensePlate(driver.getLicensePlate());
        resp.setDrivingLicense(driver.getDrivingLicense());
        resp.setIdCard(driver.getIdCard());
        resp.setVehicleType(type.getName());
        return resp;
    }

    @Override
    public User findByEmail(String email) {           //todo xoa
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Không tìm thấy user , email: " + email));
    }

    @Override
    public UserResponse getUserInfo(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Không tìm thấy user , email: " + email));
        return new UserResponse(user.getId(), user.getEmail(), user.getPhoneNumber(), user.getCreateDate(), user.getIsNonBlock(), user.getAvatarUrl(), user.getFirstRole().getName());
    }

    @Override
    public RegisterCustomerResponse getCustomerInfo(String email) {
        return null;
    }

    @Transactional
    @Override
    public User registerUser(String email, String phoneNumber, MultipartFile avatar, RoleEnum roleEnum) {
        User user = new User();
        Date currentDate = new Date();
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setIsNonBlock(true);
        user.setCreateDate(currentDate);
        Set<Role> roles = new HashSet<>();
        Role role = roleRepository.findByName(roleEnum).orElseThrow(() -> new NotFoundException("Khong tim thay role"));
        roles.add(role);
        user.setRoles(roles);
        if (avatar == null && roleEnum.equals(RoleEnum.DRIVER))
            throw new BadRequestException("Avatar khong duoc null");
        if (avatar != null) {
            String url = fileStorageService.createImgUrl(avatar);
            user.setAvatarUrl(url);
        }
        return user;
    }

	@Override
	public void UpdateUserIsNonBlock(boolean isnonblock , int id) {
		User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
		userRepository.UpdateIsNonBlock(isnonblock,user.getId());
	}
    
}
