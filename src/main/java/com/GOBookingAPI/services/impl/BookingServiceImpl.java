package com.GOBookingAPI.services.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.GOBookingAPI.entities.*;
import com.GOBookingAPI.enums.RoleEnum;
import com.GOBookingAPI.enums.VehicleType;
import com.GOBookingAPI.exceptions.AccessDeniedException;
import com.GOBookingAPI.exceptions.BadRequestException;
import com.GOBookingAPI.mapper.BookingMapper;
import com.GOBookingAPI.payload.response.*;
import com.GOBookingAPI.payload.vietmap.Route;
import com.GOBookingAPI.payload.vietmap.VietMapResponse;
import com.GOBookingAPI.repositories.DriverRepository;
import com.GOBookingAPI.services.IWebSocketService;
import com.GOBookingAPI.utils.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.request.BookingCancelRequest;
import com.GOBookingAPI.payload.request.BookingRequest;
import com.GOBookingAPI.repositories.BookingRepository;
import com.GOBookingAPI.repositories.CustomerRepository;
import com.GOBookingAPI.repositories.MyUserRepository;
import com.GOBookingAPI.repositories.projection.StatisticsBookingAmountMonthProjection;
import com.GOBookingAPI.repositories.projection.StatisticsBookingBaseProjection;
import com.GOBookingAPI.repositories.projection.StatisticsBookingCountAndSumProjections;
import com.GOBookingAPI.repositories.projection.StatisticsPaymentDayProjection;
import com.GOBookingAPI.repositories.projection.StatisticsPaymentMonthProjection;
import com.GOBookingAPI.services.IBookingService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

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

    @Autowired
    private DriverRepository driverRepository;

    @Override
    public BookingResponse createBooking(String username, BookingRequest req) {
        User user = myUserRepository.findByEmail(username).orElseThrow(() -> new NotFoundException("Không tìm thấy khách hàng"));
        Customer customer = customerRepository.findById(user.getId()).orElseThrow(() -> new NotFoundException("Không tìm thấy Customer"));

        VietMapResponse vietMapResponse = mapService.getRoute(req.getPickUpLocation(), req.getDropOffLocation(), req.getVehicleType().name());
        if (vietMapResponse.getCode().equals("ERROR")) {
            throw new BadRequestException("pickUpLocation or dropOffLocation is invalid");
        }

        System.out.println("VietMapResponse.getFirstPath().getDistance(): " + vietMapResponse.getFirstPath().getDistance());

        if (vietMapResponse.getFirstPath().getDistance() <= 200)  //     quảng đường bé hơn 200m
            throw new BadRequestException("Quảng đường quá gần, chúng tôi chưa hỗ trợ");

        if (vietMapResponse.getFirstPath().getDistance() >= 150000)  //     quảng đường lớn hơn 150km
            throw new BadRequestException("Quảng đường quá xa, chúng tôi chưa hỗ trợ");

        long amount = this.calculatePrice(vietMapResponse.getFirstPath().getDistance(), req.getVehicleType());

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setDriver(null);
        booking.setStatus(BookingStatus.WAITING);
        booking.setPickUpLocation(req.getPickUpLocation());
        booking.setDropOffLocation(req.getDropOffLocation());
        booking.setAmount(amount);
        booking.setVehicleType(req.getVehicleType());
        booking.setCreateAt(new Date());
        booking.setPickUpAddress(mapService.convertLocationToAddress(req.getPickUpLocation()));
        booking.setDropOffAddress(mapService.convertLocationToAddress(req.getDropOffLocation()));
        bookingRepository.save(booking);
        return BookingMapper.bookingToBookingResponse(booking, user);
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
            Route route = travel.getFirstPath();
            long total = this.calculatePrice(route.getDistance(), type);
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
        return BookingMapper.bookingToBookingResponse(booking, user);
    }

    @Override
    public PagedResponse<BookingResponse> getListBookingByUser(String email, Date from, Date to, int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);
        PageRequest pageable = PageRequest.of(page, size);
        User user = myUserRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
        Page<Booking> bookingPage = switch (user.getFirstRole().getName()) {
            case CUSTOMER -> bookingRepository.findBookingBetweenAndCustomer(from, to, user.getId(), pageable);
            case DRIVER -> bookingRepository.findBookingBetweenAndDriver(from, to, user.getId(), pageable);
            case ADMIN -> bookingRepository.findBookingBetween(from, to, pageable);
        };

        List<BookingResponse> bookingResponses = bookingPage.getContent().stream()
                .map(booking -> BookingMapper.bookingToBookingResponse(booking, user))
                .collect(Collectors.toList());
        return new PagedResponse<>(bookingResponses, bookingPage.getNumber(), bookingPage.getSize(),
                bookingPage.getTotalElements(), bookingPage.getTotalPages(), bookingPage.isLast());
    }

    @Override           // use for ADMIN
    public BookingResponse changeBookingStatusForAdmin(int bookingId, BookingStatus status) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Khong tim thay booking"));
        return BookingMapper.bookingToBookingResponse(booking, null);
    }

    @Override
    @Transactional
    public Booking cancelBookingForCustomer(String email, int bookingId, BookingCancelRequest req) {
        User user = myUserRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Không tìm thấy user: " + email));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Khong tim thay booking: " + bookingId));

        if (user.getId() != (booking.getCustomer().getUser().getId())) {
            throw new AccessDeniedException("Booking này không thuộc về bạn");
        }

        BookingStatus currentStatus = booking.getStatus();
        BookingStatus requestedStatus = BookingStatus.CANCELLED;

        if (currentStatus == BookingStatus.CANCELLED) {
            throw new BadRequestException("Đơn đặt đã bị hủy trước đó");
        }

        switch (currentStatus) {
            case COMPLETE:
                throw new BadRequestException("Chuyến đi đã hoàn tất không thể hủy");
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
            case FOUND:
            case ARRIVED_PICKUP:
                requestedStatus = BookingStatus.WAITING_REFUND;
                break;
        }

        booking.setStatus(requestedStatus);
        booking.setReasonType(req.getReasonType());
        booking.setContentCancel(req.getContent());
        bookingRepository.save(booking);

        if (booking.getDriver() != null) {
            if(booking.getStatus().equals(BookingStatus.COMPLETE) || booking.getStatus().equals(BookingStatus.WAITING_REFUND)){
                managerBooking.deleteData(booking.getDriver().getId());
                if(booking.getDriver().getStatus().equals(DriverStatus.ON_RIDE)){
                    booking.getDriver().setStatus(DriverStatus.FREE);
                    driverRepository.save(booking.getDriver());
                }
            }
        }
        // Trả về thông tin trạng thái mới của đơn đặt
        return booking;
    }

    @Override
    public BaseResponse<Booking> Confirm(int id) {
        try {
            return null;
        } catch (Exception e) {
            log.info("error in Booking Service");
            return null;
        }
    }

    @Override
    public BaseResponse<Booking> Cancel(BookingCancelRequest req) {
        try {
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
    public boolean isDriverBelongsToCustomerBooking(User cus, int driverId) {
        List<Booking> bookingList = bookingRepository.findByCustomerId(cus.getId(), driverId);
        return bookingList.size() > 0;
    }

    @Override
    @Transactional
    public Booking changeBookingStatusAndNotify(String email, int bookingId, BookingStatus newStatus) {
        User user = myUserRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Khong tim thay booking"));

        if (user.getFirstRole().getName().equals(RoleEnum.CUSTOMER)) {
            if (booking.getCustomer().getId() != user.getId()) {
                System.out.println("==>Fail, Booking khong thuoc ve ban: " + bookingId);
                return null;
            }

            if (!newStatus.equals(BookingStatus.CANCELLED)) {
                System.out.println("==>Fail, booking status not permit: " + newStatus);
                return null;
            }

            if (booking.getStatus() != BookingStatus.WAITING && booking.getStatus() != BookingStatus.PAID && booking.getStatus() != BookingStatus.FOUND && booking.getStatus() != BookingStatus.ARRIVED_PICKUP) {
                System.out.println("==> booking.getStatus() != BookingStatus.WAITING || booking.getStatus() != BookingStatus.PAID");
                return null;
            }

            if (booking.getStatus().equals(BookingStatus.PAID)) {
                booking.setStatus(BookingStatus.WAITING_REFUND);
            } else {
                booking.setStatus(BookingStatus.CANCELLED);
            }
        }

        if (user.getFirstRole().getName().equals(RoleEnum.DRIVER)) {
            if (booking.getDriver().getId() != user.getId()) {
                System.out.println("==>Fail, Booking khong thuoc ve ban: " + bookingId);
                return null;
            }

            if (!booking.getStatus().equals(BookingStatus.FOUND) && newStatus.equals(BookingStatus.ARRIVED_PICKUP)) {
                System.out.println("==>Trạng thái thay đổi không hợp lệ không thể từ : " + booking.getStatus() + "=> " + newStatus);
                return null;
            }

            if (!booking.getStatus().equals(BookingStatus.ARRIVED_PICKUP) && newStatus.equals(BookingStatus.ON_RIDE)) {
                System.out.println("==>Trạng thái thay đổi không hợp lệ không thể từ : " + booking.getStatus() + "=> " + newStatus);
                return null;
            }

            if (!booking.getStatus().equals(BookingStatus.ON_RIDE) && newStatus.equals(BookingStatus.COMPLETE)) {
                System.out.println("==>Trạng thái thay đổi không hợp lệ không thể từ : " + booking.getStatus() + "=> " + newStatus);
                return null;
            }
            booking.setStatus(newStatus);
        }

        if (user.getFirstRole().getName().equals(RoleEnum.ADMIN)) {
            booking.setStatus(newStatus);
        }

        if (booking.getDriver() != null) {
            if(booking.getStatus().equals(BookingStatus.COMPLETE) || booking.getStatus().equals(BookingStatus.WAITING_REFUND)){
                managerBooking.deleteData(booking.getDriver().getId());
                if(booking.getDriver().getStatus().equals(DriverStatus.ON_RIDE)){
                    booking.getDriver().setStatus(DriverStatus.FREE);
                    driverRepository.save(booking.getDriver());
                }
            }
        }
        return bookingRepository.save(booking);
    }

    @Override
    public BookingResponse getCurrentBooking(User user) {
        Optional<Booking> bookingOptional = bookingRepository.getCurrentActiveBooking(user.getId(), user.getFirstRole().getName().name());
        return bookingOptional.map(booking -> BookingMapper.bookingToBookingResponse(booking, user)).orElse(null);
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public PagedResponse<BookingResponse> filterBookings(Date from, Date to, BookingStatus status, String sortType,
                                                         String sortField, int page, int size, String email) {
        User user = myUserRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Không tìm thấy user"));

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Booking> criteriaQuery = criteriaBuilder.createQuery(Booking.class);
        Root<Booking> root = criteriaQuery.from(Booking.class);

        List<Predicate> predicates = new ArrayList<>();
        if (from != null && to != null) {
            Path<Date> fieldCreateAt = root.get("createAt");
            Predicate predicate1 = criteriaBuilder.greaterThanOrEqualTo(fieldCreateAt, from);
            Predicate predicate2 = criteriaBuilder.lessThanOrEqualTo(fieldCreateAt, to);
            predicates.add(predicate1);
            predicates.add(predicate2);
        }

        if (status != null) {
            Path<BookingStatus> fieldStatus = root.get("status");
            Predicate predicate = criteriaBuilder.equal(fieldStatus, status);
            predicates.add(predicate);
        }

        if (sortField == null || sortField.isBlank()) {
            sortField = "amount";
        }
        if (sortType == null)
            sortType = "asc";

        Path<Object> sortRoute = null;
        try {
            sortRoute = root.get(sortField);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid sortField: " + sortField);
        }

        Order order = "asc".equalsIgnoreCase(sortType) ? criteriaBuilder.asc(sortRoute) : criteriaBuilder.desc(sortRoute);
        criteriaQuery.orderBy(order);

        if (user.getFirstRole().getName().equals(RoleEnum.CUSTOMER)) {
            Path<Integer> fieldCusId = root.get("customer").get("id");
            Predicate predicate = criteriaBuilder.equal(fieldCusId, user.getId());
            predicates.add(predicate);
        }

        if (user.getFirstRole().getName().equals(RoleEnum.DRIVER)) {
            Path<Integer> fieldDriverId = root.get("driver").get("id");
            Predicate predicate = criteriaBuilder.equal(fieldDriverId, user.getId());
            predicates.add(predicate);
        }

        criteriaQuery.select(root).where(predicates.toArray(new Predicate[0]));
        TypedQuery<Booking> typedQuery = entityManager.createQuery(criteriaQuery);

        AppUtils.validatePageNumberAndSize(page, size);

        int totalResults = typedQuery.getResultList().size();

        typedQuery.setFirstResult(page * size);
        typedQuery.setMaxResults(size);
        List<Booking> books = typedQuery.getResultList();
        PageRequest pageRequest = PageRequest.of(page, size);

        List<BookingResponse> bookingResponses = books.stream()
                .map(booking -> BookingMapper.bookingToBookingResponse(booking, user))
                .collect(Collectors.toList());

        Page<BookingResponse> pagedResponse = new PageImpl<>(bookingResponses, pageRequest, totalResults);
        return new PagedResponse<>(pagedResponse.getContent(), pagedResponse.getNumber(), pagedResponse.getSize(),
                pagedResponse.getTotalElements(), pagedResponse.getTotalPages(), pagedResponse.isLast());
    }

	@Override
	public StatisticsBookingBaseResponse getStatisticsBookingDate(Date from, Date to, String statisticsType, int size,
			int page) {
		List<StatisticsBookingDateResponse> statisticsBookingDayResponses = new ArrayList<StatisticsBookingDateResponse>();
		int totalBooking=0 ;
		int totalBookingSuccess =0;
		int totalBookingFail =0;

		int totalResuls =0;
		if(statisticsType == null) {
			statisticsType = "day";
		}
		switch (statisticsType) {
		case "month": {
			if(from == null || to == null) {
				Calendar calendar1 = Calendar.getInstance();
				calendar1.add(Calendar.MONTH, 1);
				calendar1.add(Calendar.YEAR, -1);
				from = calendar1.getTime();
				
				Calendar calendar2 = Calendar.getInstance();
				calendar2.add(Calendar.MONTH, 12);
				calendar2.add(Calendar.YEAR, -1);
				to = calendar2.getTime();
				
			}
			Calendar calFrom = Calendar.getInstance();
			calFrom.setTime(from);
			int monthFrom = calFrom.get(Calendar.MONTH) + 1;
			
			Calendar calTo= Calendar.getInstance();
			calTo.setTime(to);
			int monthTo = calTo.get(Calendar.MONTH) +1;
			
			int yearFrom = calFrom.get(Calendar.YEAR);
			int yearTo = calFrom.get(Calendar.YEAR);
			log.info("from {} {} to {} {}" ,monthFrom, yearFrom , monthTo ,yearTo);
			
			
			List<StatisticsBookingCountAndSumProjections> StatisticsMonth = bookingRepository.getCountAndSumMonth(monthFrom, monthTo,yearFrom ,yearTo);
		
			List<StatisticsBookingAmountMonthProjection> StatisticsDayOfMonthSuccess = bookingRepository.getAmuontWithStatus(monthFrom, monthTo,BookingStatus.COMPLETE.toString(), yearFrom ,yearTo);
			List<StatisticsBookingAmountMonthProjection> StatisticsDayOfMonthCanclled = bookingRepository.getAmuontWithStatus(monthFrom, monthTo,BookingStatus.CANCELLED.toString(), yearFrom ,yearTo);

			List<StatisticsBookingAmountMonthProjection> StatisticsDayOfMonthRefunded = bookingRepository.getAmuontWithStatus(monthFrom, monthTo,BookingStatus.REFUNDED.toString(), yearFrom ,yearTo);
			for(int i = 0 ; i< StatisticsMonth.size() ; i++) {
				totalBooking += StatisticsMonth.get(i).getCount();
				totalBookingSuccess += StatisticsDayOfMonthSuccess.get(i).getCount();
				totalBookingFail += StatisticsDayOfMonthCanclled.get(i).getCount() + StatisticsDayOfMonthRefunded.get(i).getCount();				double distance = 0;
				double avgMonth = 0;
				try {
					avgMonth = StatisticsDayOfMonthSuccess.get(i).getAmount()/StatisticsMonth.get(i).getCount();
				} catch (Exception e) {
					avgMonth = 0;
				}
				statisticsBookingDayResponses.add(new StatisticsBookingDateResponse(StatisticsMonth.get(i).getDay(),
																					StatisticsMonth.get(i).getCount(), 
																					StatisticsMonth.get(i).getTotal(),
																					StatisticsDayOfMonthSuccess.get(i).getAmount(), 
																					StatisticsDayOfMonthCanclled.get(i).getAmount( )+ StatisticsDayOfMonthRefunded.get(i).getAmount(),
																					avgMonth));
			}
			totalResuls =StatisticsMonth.size();
			break;
		}
		
		case "day" :{
			if(from == null || to == null) {
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DAY_OF_MONTH, -7);
				from = calendar.getTime();
				to = new Date();
			}
			List<StatisticsBookingCountAndSumProjections> projection = bookingRepository.getCountAndSumDay(from, to);
			List<StatisticsBookingBaseProjection> baseBooking = bookingRepository.getBaseBooking(from,to);
			
			
			for(int i = 0 ; i< projection.size() ; i++) {
				totalBooking += projection.get(i).getCount();
				int numberStatus = 0; 
				int amountSuccess = 0;
				int amountFail =0;
				for(int j =0 ; j< baseBooking.size(); j++) {
					if(baseBooking.get(j).getDay().equals(projection.get(i).getDay())) {
						numberStatus +=1;
						if(baseBooking.get(j).getStatus().equals(BookingStatus.COMPLETE)) {
								
							amountSuccess += baseBooking.get(j).getAmount();
							totalBookingSuccess+=1;
						}
						else {
							amountFail += baseBooking.get(j).getAmount();
							totalBookingFail +=1;
						}
					}
				}
				double avg_day = 0;
				try {
					 avg_day = amountSuccess/numberStatus;
				} catch (Exception e) {
					avg_day = 0;
				}
				statisticsBookingDayResponses.add(new StatisticsBookingDateResponse(projection.get(i).getDay(),
																				   projection.get(i).getCount(),
																				   projection.get(i).getTotal(),
																				   amountSuccess,
																				   amountFail,
																				   avg_day));
			}
			totalResuls = projection.size();
			break;
		}
		default:
			throw new BadRequestException("Invalid statisticsField ");
		}
		PageRequest pageRequest = PageRequest.of(page,size);
		Page<StatisticsBookingDateResponse> pagedResponse = new PageImpl<>(statisticsBookingDayResponses, pageRequest, totalResuls);
		PagedResponse<StatisticsBookingDateResponse> StatisticsBookingPagedResponse = new PagedResponse<StatisticsBookingDateResponse>(pagedResponse.getContent(),pagedResponse.getNumber(),pagedResponse.getSize(),
																										   pagedResponse.getTotalElements(),pagedResponse.getTotalPages(),pagedResponse.isLast());
		return new StatisticsBookingBaseResponse(totalBooking,totalBookingSuccess, totalBookingFail, StatisticsBookingPagedResponse);
	}

    
    
    
}
