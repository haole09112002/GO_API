package com.GOBookingAPI.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.GOBookingAPI.payload.request.ReviewRequest;
import com.GOBookingAPI.services.IReviewService;

@RestController
@RequestMapping("/reivews")
public class ReviewController {

	@Autowired
	private IReviewService reviewService;
	
	@PostMapping("/")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<?> createReview(@RequestBody ReviewRequest reviewRequest){
		return ResponseEntity.ok(reviewService.createReview(reviewRequest));
	}
}
