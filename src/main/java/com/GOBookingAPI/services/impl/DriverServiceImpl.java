package com.GOBookingAPI.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.enums.ReasonType;
import com.GOBookingAPI.enums.RoleEnum;
import com.GOBookingAPI.enums.VehicleType;
import com.GOBookingAPI.exceptions.AccessDeniedException;
import com.GOBookingAPI.payload.dto.BookingStatistic;
import com.GOBookingAPI.payload.response.*;
import com.GOBookingAPI.repositories.UserRepository;
import com.GOBookingAPI.repositories.projection.UserDriverProjection;
import com.GOBookingAPI.services.IBookingService;
import com.GOBookingAPI.utils.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.enums.BookingStatus;

import com.GOBookingAPI.exceptions.BadRequestException;
import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.vietmap.Route;
import com.GOBookingAPI.payload.vietmap.VietMapResponse;
import com.GOBookingAPI.repositories.BookingRepository;
import com.GOBookingAPI.repositories.DriverRepository;
import com.GOBookingAPI.services.ConversationService;
import com.GOBookingAPI.services.IDriverService;
import com.GOBookingAPI.services.IWebSocketService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
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

	@PersistenceContext
	private EntityManager entityManager;

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

			Route route = travel.getFirstPath();
			if (route.getDistance() < minDistance) {
				minDistance = route.getDistance();
				id_driver = driver.getDriverId();
			}
		}
//        System.out.println("==> founded driver " + id_driver);
		return driverRepository.findById(id_driver).orElse(null);
	}

	@Override
	public void scheduleFindDriverTask(Booking booking, String locationCustomer) {
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(() -> {
			Booking updateBooking = bookingRepository.findById(booking.getId())
					.orElseThrow(() -> new NotFoundException("khong tim thay booking"));
			if (updateBooking.getStatus().equals(BookingStatus.CANCELLED)
					|| updateBooking.getStatus().equals(BookingStatus.WAITING_REFUND)) {
				executorService.shutdown();
				return;
			}

			boolean driverFound = findAndNotifyDriver(updateBooking, locationCustomer);

			if (driverFound) {
				executorService.shutdown();
				return;
			}

			if (AppUtils.currentTimeInSecond()
					- updateBooking.getCreateAt().getTime() / 1000 > AppConstants.MAX_TIME_PENDING) {
				updateBooking.setStatus(BookingStatus.WAITING_REFUND);
				bookingRepository.save(updateBooking);
				System.out.println(
						"==> Time out booking id:" + updateBooking.getId() + ", status: " + updateBooking.getStatus());
				webSocketService.notifyBookingStatusToCustomer(updateBooking.getCustomer().getId(),
						new BookingStatusResponse(updateBooking.getId(), updateBooking.getStatus()));
				executorService.shutdown();
			}
		}, AppConstants.INIT_DELAY, AppConstants.PERIOD_TIME, TimeUnit.SECONDS);
	}

	@Override
	@Transactional
	public boolean findAndNotifyDriver(Booking booking, String locationCustomer) {
		Driver driverChosen = findDriverBooking(locationCustomer, booking.getVehicleType());
		if (driverChosen == null) {
			System.out.println("Find driver null for booking id: " + booking.getId());
			return false;
		}

		if(driverChosen.getStatus().equals(DriverStatus.BLOCK)){				//todo
			System.out.println("Find driver null for booking id: " + booking.getId());
			return false;
		}

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
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new NotFoundException("Không tìm thấy user , email: " + email));
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

			Driver driver = driverRepository.findById(driverId)
					.orElseThrow(() -> new NotFoundException("Không tìm thấy driver , driver: " + email));
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
		if (bookingService.isDriverBelongsToCustomerBooking(user, driverId)) {
			Driver driver = driverRepository.findById(driverId)
					.orElseThrow(() -> new NotFoundException("Không tìm thấy driver , driverId: " + driverId));

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

	/*
	    @author: HaoLV
	    @description: thay doi trang thai tai xe online hay offline
	*/

	@Override
	public DriverStatusResponse changeDriverStatus(String email, Integer driverId) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new NotFoundException("Không tìm thấy user , email: " + email));
		Driver driver = user.getDriver();
		if(driver.getId() != driverId)
			throw new NotFoundException("Không tìm thấy driver: " + driverId);

		if(driver.getStatus() != DriverStatus.FREE && driver.getStatus() != DriverStatus.OFF){
			throw new BadRequestException("Không thể thay đổi trạng thái, trạng thái hiện tại: " + driver.getStatus());
		}

		if(driver.getStatus() == DriverStatus.FREE){
			driver.setStatus(DriverStatus.OFF);
		}else {
			driver.setStatus(DriverStatus.FREE);

		}
		driverRepository.save(driver);
		return new DriverStatusResponse(driverId, driver.getStatus());
	}

	@Override
	public Driver getById(int id) {
		return driverRepository.findById(id).orElseThrow(() -> new NotFoundException("Khong tim thay driver, driverId: " + id));
	}

	@Override
	public PagedResponse<DriverPageResponse> getDriverPageAndSort(Date from, Date to, Boolean isNonBlock,
			DriverStatus status, String searchField, String keyword, String sortType, String sortField, int size,
			int page) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
		Root<Driver> root = criteriaQuery.from(Driver.class);

		Join<Driver, User> userJoin = root.join("user", JoinType.INNER);

		criteriaQuery.multiselect(userJoin.get("id").alias("Id"),
									userJoin.get("createDate").alias("CreateDate"),
									root.get("activityArea").alias("Area"),
									userJoin.get("email").alias("Email"),
									root.get("fullName").alias("FullName"),
									userJoin.get("phoneNumber").alias("PhoneNumber"),
									root.get("status").alias("Status"),
									userJoin.get("isNonBlock").alias("IsNonBlock"));

		List<Predicate> predicates = new ArrayList<Predicate>();
		if (from != null && to != null) {
			Path<Date> fieldcreateDate = userJoin.get("createDate");
			Predicate predicate1 = criteriaBuilder.greaterThanOrEqualTo(fieldcreateDate, from);
			Predicate predicate2 = criteriaBuilder.lessThanOrEqualTo(fieldcreateDate, to);
			predicates.add(predicate1);
			predicates.add(predicate2);
		}

		Path<Boolean> fieldIsNonBlock = userJoin.get("isNonBlock");
		Predicate predicateboolean = criteriaBuilder.equal(fieldIsNonBlock, isNonBlock);
		predicates.add(predicateboolean);

		if (sortField == null) {
			sortField = "id";
		}

		if (sortType == null) {
			sortType = "asc";
		}

		if(status != null) {
			Path<DriverStatus> fieldstatus = root.get("status");
			Predicate predicate = criteriaBuilder.equal(fieldstatus, status);
			predicates.add(predicate);
		}

		if(status == null) {
			Path<DriverStatus> fieldstatus = root.get("status");
			List<DriverStatus> statusList = Arrays.asList(DriverStatus.NOT_ACTIVATED, DriverStatus.REFUSED);
			Predicate predicate = criteriaBuilder.not(fieldstatus.in(statusList));
			predicates.add(predicate);
		}

		if(status == null) {
			Path<DriverStatus> fieldstatus = root.get("status");
			List<DriverStatus> statusList = Arrays.asList(DriverStatus.NOT_ACTIVATED, DriverStatus.REFUSED);
			Predicate predicate = criteriaBuilder.not(fieldstatus.in(statusList));
			predicates.add(predicate);

		}

		Path<Object> sortRoute = null;

		try {
			if(sortField.equals("fullName")) {
				sortRoute = root.get(sortField);
			}else {
				sortRoute = userJoin.get(sortField);
			}

		} catch (IllegalArgumentException e) {
			throw new BadRequestException("Invalid sortField" + sortField);
		}
		Order order = "asc".equalsIgnoreCase(sortType) ? criteriaBuilder.asc(sortRoute)
				: criteriaBuilder.desc(sortRoute);
		criteriaQuery.orderBy(order);
		if (keyword != null) {
			if (searchField.equals("email")) {
				Path<String> fieldEmail = userJoin.get("email");
				Predicate predicate = criteriaBuilder.like(criteriaBuilder.lower(fieldEmail),
						"%" + keyword.toLowerCase() + "%");
				predicates.add(predicate);
			} else if (searchField.equals("fullName")) {
				Path<String> fieldName = root.get("fullName");
				Predicate predicate = criteriaBuilder.like(criteriaBuilder.lower(fieldName),
						"%" + keyword.toLowerCase() + "%");
				predicates.add(predicate);
			} else if (searchField.equals("phoneNumber")) {
				Path<String> fieldPhone = userJoin.get("phoneNumber");
				Predicate predicate = criteriaBuilder.like(fieldPhone, keyword.toLowerCase() + "%");
				predicates.add(predicate);
			} else if(searchField.equals("activityArea")){
				Path<String> fieldArea = root.get("activityArea");
				Predicate predicate = criteriaBuilder.like(fieldArea, "%" + keyword.toLowerCase() + "%");
				predicates.add(predicate);
			}else {
				throw new BadRequestException("Invalid field" + searchField);
			}
		}

		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		TypedQuery<Tuple> typedQuery = entityManager.createQuery(criteriaQuery);

		AppUtils.validatePageNumberAndSize(page, size);

		int totalResults = typedQuery.getResultList().size();

		typedQuery.setFirstResult(page * size);
		typedQuery.setMaxResults(size);

		List<DriverPageResponse> drivers = typedQuery.getResultList().stream()
				.map(result -> new DriverPageResponse((int) result.get("Id"),
													  (String) result.get("Email"),
													  (Date) result.get("CreateDate"),
													  (String) result.get("Area"),
													  (String) result.get("FullName"),
													  (String) result.get("PhoneNumber"),
													  (DriverStatus) result.get("Status"),
													  (Boolean) result.get("IsNonBlock")))
								.collect(Collectors.toList());
		PageRequest pageRequest = PageRequest.of(page, size);
		Page<DriverPageResponse> pageResponse = new PageImpl<DriverPageResponse>(drivers, pageRequest, totalResults);

		return new PagedResponse<DriverPageResponse>(pageResponse.getContent(), pageResponse.getNumber(),
				pageResponse.getSize(), pageResponse.getTotalElements(), pageResponse.getTotalPages(),
				pageResponse.isLast());

	}

	@Override
	public DriverActiveResponse ActiveOrRefuseDriver(String ids,String type) {
		List<String> error = new ArrayList<String>();
		String[] idsString = ids.split(",");
		List<Integer> list = new ArrayList<Integer>();
		for (String i : idsString) {
			if (isInteger(i)) {
				list.add(Integer.parseInt(i));
			} else {
				error.add(i + " is not idDrvier,");
			}

		}
		if (list.isEmpty()) {
			StringBuilder notify = new StringBuilder();
			error.forEach(e -> notify.append(e));
			notify.deleteCharAt(notify.length()-1);
			return new DriverActiveResponse("Fail", notify.toString());
		} else {
			for (int i = 0 ; i< list.size(); i++) {
				UserDriverProjection projection = driverRepository.getStatusAndIsNonBlock(list.get(i));
				if(projection == null) {
					error.add("driver with id " + String.valueOf(list.get(i)) + " is not exits,");
					list.remove(i);
					i--;
				}else {
					if (!projection.getisNonBlock()) {
						error.add("driver with id " + String.valueOf(list.get(i)) + " is blocked,");
						list.remove(i);
						i--;
					}else {
						if(projection.getStatus().equals(DriverStatus.REFUSED)) {
							error.add("driver with id " + String.valueOf(list.get(i)) + " refused,");
							list.remove(i);
							i--;
						}else if (!projection.getStatus().equals(DriverStatus.NOT_ACTIVATED)) {
							error.add("driver with id " + String.valueOf(list.get(i)) + " activated,");
							list.remove(i);
							i--;
						}
					}
				}
			}
			if(list.isEmpty()) {
				StringBuilder notify = new StringBuilder();
				error.forEach(e -> notify.append(e));
				notify.deleteCharAt(notify.length()-1);
				return new DriverActiveResponse("Fail", notify.toString());
			}else {
				if(type.equals(AppConstants.ACTIVE))
					driverRepository.activeDriver(list);
				else
					driverRepository.refuseDriver(list);
			}
			if (!error.isEmpty()) {
				StringBuilder notify = new StringBuilder();
				error.forEach(e -> notify.append(e));
				notify.deleteCharAt(notify.length()-1);
				return new DriverActiveResponse("Warming", notify.toString());
			} else {
				if(type.equals(AppConstants.ACTIVE))
					return new DriverActiveResponse("Succesfull" ,"All drivers activated");
				else
					return new DriverActiveResponse("Succesfull" ,"All drivers refused");
			}
		}

	}

	private boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public DriverActiveResponse blockStatus(int id,Boolean isBlock) {

				UserDriverProjection projection = driverRepository.getStatusAndIsNonBlock(id);
				if(projection == null) {
					throw new BadRequestException("driver is not exits");
				}else {
					if (!projection.getisNonBlock()) {
						throw new BadRequestException("driver have account is blocked");
					}else {
						if(projection.getStatus().equals(DriverStatus.REFUSED)) {
							throw new BadRequestException("driver refused");
						}else {
							if(projection.getStatus().equals(DriverStatus.BLOCK) && isBlock) {
								throw new BadRequestException("driver blocked");
							}else if(!projection.getStatus().equals(DriverStatus.BLOCK) && !isBlock) {
								throw new BadRequestException("driver non blocked");
							}
						}
					}

				}


				if(isBlock) {

					driverRepository.blockDriver(id);
					return new DriverActiveResponse("Succesfull" ,"Driver blocked");
				}
				else {
					driverRepository.nonBlockDriver(id);
					return new DriverActiveResponse("Succesfull" ,"Driver nonBlocked");
				}
	}

    public BookingStatisticResponse bookingStatisticByDriver(String email, Date from, Date to, Integer id) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy user , email: " + email)); 
        Driver driver = null;
        if(id != null){
            if( user.getFirstRole().getName().equals(RoleEnum.ADMIN)){
                driver = driverRepository.findById(id).orElseThrow(() -> new NotFoundException("Khong tim yhay driver: " + id));
            }else
                throw new AccessDeniedException("Login with role admin to access");
        }else {
            if(user.getFirstRole().getName().equals(RoleEnum.DRIVER)){
                driver = user.getDriver();
            }else
                throw new BadRequestException("If role Admin, require PathVariable can't be null");
        }
        BookingStatistic bookingStatistic = driverRepository.statisticalBooking(from, to, driver.getId());
        return new BookingStatisticResponse(bookingStatistic, driver.getFirstVehicleType().getName().getPercent());
    }
}
