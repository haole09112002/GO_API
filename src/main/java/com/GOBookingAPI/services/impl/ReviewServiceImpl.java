package com.GOBookingAPI.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.enums.RoleEnum;
import com.GOBookingAPI.exceptions.AccessDeniedException;
import com.GOBookingAPI.exceptions.BadRequestException;
import com.GOBookingAPI.payload.response.ReviewResponse;
import com.GOBookingAPI.payload.response.StatisticsBookingBaseResponse;
import com.GOBookingAPI.payload.response.StatisticsBookingDateResponse;
import com.GOBookingAPI.payload.response.StatisticsReviewDateResponse;
import com.GOBookingAPI.payload.response.StatisticsReviewResponse;
import com.GOBookingAPI.repositories.*;
import com.GOBookingAPI.repositories.projection.StatisticsBookingAmountMonthProjection;
import com.GOBookingAPI.repositories.projection.StatisticsBookingBaseProjection;
import com.GOBookingAPI.repositories.projection.StatisticsBookingCountAndSumProjections;
import com.GOBookingAPI.repositories.projection.StatisticsReviewBaseProjection;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Review;
import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.request.ReviewRequest;
import com.GOBookingAPI.payload.response.BaseResponse;
import com.GOBookingAPI.payload.response.PagedResponse;
import com.GOBookingAPI.services.IReviewService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReviewServiceImpl implements IReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Override
    @Transactional
    public ReviewResponse createReview(ReviewRequest reviewRequest, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Khong tim thay user: " + email));
        Booking booking = bookingRepository.findById(reviewRequest.getBookingId()).orElseThrow(() -> new NotFoundException("Khong tim thay booking: " + reviewRequest.getBookingId()));

        if (booking.getCustomer().getId() != user.getId()) {
            throw new AccessDeniedException("You don't have permit to access this resource");
        }

        if (!booking.getStatus().equals(BookingStatus.COMPLETE)) {
            throw new BadRequestException("Booking not yet complete");
        }

        if (booking.getReview() != null) {
            throw new BadRequestException("You already reviewed this booking " + reviewRequest.getBookingId());
        }

        Review review = new Review();
        review.setId(booking.getId());
        review.setBooking(booking);
        review.setRating(reviewRequest.getRating());
        review.setContent(reviewRequest.getContent());
        review.setCreateAt(new Date());
        reviewRepository.save(review);

        Driver driver = new Driver();
        driver.updateRating(review.getRating());
        driverRepository.save(driver);

        return new ReviewResponse(review);
    }

    @Override
    public ReviewResponse getReviewById(int id, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Khong tim thay user: " + email));
        Review review = reviewRepository.findById(id).orElseThrow(()-> new NotFoundException("Khong tim thay review: " + id));
        if(user.getFirstRole().getName().equals(RoleEnum.CUSTOMER))
            if(review.getBooking().getCustomer().getId() != user.getId())
                throw new AccessDeniedException("Review khong thuoc ve ban");

        if(user.getFirstRole().getName().equals(RoleEnum.DRIVER))
            if(review.getBooking().getDriver().getId() != user.getId())
                throw new AccessDeniedException("Review khong thuoc ve ban");

        return new ReviewResponse(review);
    }

	@Override
	public StatisticsReviewResponse getStatisticsBookingDate(Date from, Date to, String statisticsType, int size,
			int page) {
		List<StatisticsReviewDateResponse> statisticsRiviewDateResponses = new ArrayList<StatisticsReviewDateResponse>();
		int totalReview=0 ;
		int reviewPositive =0;
		int reviewNegative =0;

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
			
			
		
			List<StatisticsReviewBaseProjection> projection = reviewRepository.getStatisticsReviewMonth(monthFrom, monthTo,yearFrom, yearTo);
			for(int i = 0 ; i< projection.size() ; i++) {
				totalReview += projection.get(i).getCount();
				reviewPositive += projection.get(i).getFiveStar() + projection.get(i).getFourStar() + projection.get(i).getThreeStar();
				reviewNegative += projection.get(i).getTwoStar() + projection.get(i).getOneStar();
				String reviewContentPositive = reviewRepository.getReviewPositiveMonth((int) projection.get(i).getDate());
				String reviewContentNegative = reviewRepository.getReviewNegativeMonth((int) projection.get(i).getDate());
				if(reviewContentPositive == null )
					reviewContentPositive = "No content Positive";
				if(reviewContentNegative == null)
					reviewContentNegative = "No content Negative";
				statisticsRiviewDateResponses.add(new StatisticsReviewDateResponse(projection.get(i).getDate(),
																				   projection.get(i).getCount(),
																				   projection.get(i).getFiveStar(),
																				   projection.get(i).getFourStar(),
																				   projection.get(i).getThreeStar(),
																				   projection.get(i).getTwoStar(), 
																				   projection.get(i).getOneStar(),
																				   reviewContentPositive ,
																				   reviewContentNegative));
			}
			totalResuls =projection.size();
			break;
		}
		
		case "day" :{
			if(from == null || to == null) {
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DAY_OF_MONTH, -7);
				from = calendar.getTime();
				to = new Date();
			}
			List<StatisticsReviewBaseProjection> projection = reviewRepository.getStatisticsReviewDay(from, to);
			for(int i = 0 ; i< projection.size() ; i++) {
				totalReview += projection.get(i).getCount();
				reviewPositive += projection.get(i).getFiveStar() + projection.get(i).getFourStar() + projection.get(i).getThreeStar();
				reviewNegative += projection.get(i).getTwoStar() + projection.get(i).getOneStar();
				String reviewContentPositive = reviewRepository.getReviewPositiveDay((Date) projection.get(i).getDate());
				String reviewContentNegative = reviewRepository.getReviewNegativeDay((Date) projection.get(i).getDate());
				if(reviewContentPositive== null)
					reviewContentPositive = "No content Positive";
				if(reviewContentNegative== null)
					reviewContentNegative = "No content Negative";
				statisticsRiviewDateResponses.add(new StatisticsReviewDateResponse(projection.get(i).getDate(),
																				   projection.get(i).getCount(),
																				   projection.get(i).getFiveStar(),
																				   projection.get(i).getFourStar(),
																				   projection.get(i).getThreeStar(),
																				   projection.get(i).getTwoStar(), 
																				   projection.get(i).getOneStar(),
																				   reviewContentPositive ,
																				   reviewContentNegative));
			}
			totalResuls = projection.size();
			break;
		}
		default:
			throw new BadRequestException("Invalid statisticsField ");
		}
		PageRequest pageRequest = PageRequest.of(page,size);
		Page<StatisticsReviewDateResponse> pagedResponse = new PageImpl<>(statisticsRiviewDateResponses, pageRequest, totalResuls);
		PagedResponse<StatisticsReviewDateResponse> StatisticsReviewPagedResponse = new PagedResponse<StatisticsReviewDateResponse>(pagedResponse.getContent(),pagedResponse.getNumber(),pagedResponse.getSize(),
																										   pagedResponse.getTotalElements(),pagedResponse.getTotalPages(),pagedResponse.isLast());
		return new StatisticsReviewResponse(totalReview,reviewPositive, reviewNegative, StatisticsReviewPagedResponse);
	
	}
    
    
}
