package com.GOBookingAPI.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.GOBookingAPI.payload.request.ReviewRequest;
import com.GOBookingAPI.services.IReviewService;

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
}
