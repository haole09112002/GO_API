package com.GOBookingAPI.controller;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.GOBookingAPI.payload.request.ReviewRequest;
import com.GOBookingAPI.services.IReviewService;
import com.GOBookingAPI.utils.AppConstants;

@RestController
@RequestMapping("/reviews")
@Validated
public class ReviewController {

	@Autowired
	private IReviewService reviewService;

	@PostMapping
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<?> createReview(@RequestBody @Valid ReviewRequest reviewRequest){
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return ResponseEntity.ok(reviewService.createReview(reviewRequest, email));
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getReview(@PathVariable int id){
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return ResponseEntity.ok(reviewService.getReviewById(id, email));
	}
	
	@GetMapping("/statisticsdate")
    public ResponseEntity<?> getStatisticsDay(@RequestParam(name ="from" , required = false) @Nullable @DateTimeFormat(pattern =  "yyyy-MM-dd") Date from,
    											@RequestParam(name = "to"  , required = false)@Nullable @DateTimeFormat(pattern =  "yyyy-MM-dd")  Date to,
    											@RequestParam(name ="statisticsType" , required = false) String statisticsType,
    											@RequestParam(name = "size" , required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
    											@RequestParam(name = "page" , required =  false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page
    											){
    	return ResponseEntity.ok(reviewService.getStatisticsBookingDate(from, to,  statisticsType,  size,  page));
	}
}