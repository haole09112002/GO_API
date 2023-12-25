package com.GOBookingAPI.controller;

import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.enums.RoleEnum;
import com.GOBookingAPI.exceptions.BadRequestException;
import com.GOBookingAPI.payload.request.ChangeCustomerInfoRequest;
import com.GOBookingAPI.services.CustomerService;
import com.GOBookingAPI.services.IUserService;
import com.GOBookingAPI.utils.AppConstants;

import io.micrometer.common.lang.Nullable;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private IUserService userService;

	@GetMapping(value = {"/{id}", "/"})
	@PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
	public ResponseEntity<?> getCustomer(@PathVariable(required = false) int id){
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userService.getByEmail(email);
		if(user.getFirstRole().getName().equals(RoleEnum.CUSTOMER)){
			return ResponseEntity.ok(customerService.getById(user.getId()));
		}
		return ResponseEntity.ok(customerService.getById(id));
	}

	@GetMapping("/{id}/base-info")
	public ResponseEntity<?> getBaseInfoCustomer(@PathVariable int id){
		return ResponseEntity.ok(customerService.getBaseInfoById(id));
	}
	
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> getCustomers(@RequestParam(required = false) @Nullable @DateTimeFormat( pattern = "yyyy-MM-dd") Date from,
										  @RequestParam(required = false) @Nullable @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
										  @RequestParam(required = false ,defaultValue = AppConstants.IS_NON_BLOCK ) boolean isNonBlock,
										  @RequestParam(required = false) String searchField,
										  @RequestParam(required = false) String keyword,
										  @RequestParam(required = false) String sortType,
										  @RequestParam(required = false) String sortField,
										  @RequestParam(required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
										  @RequestParam(required = false , defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page){
		return ResponseEntity.ok(customerService.getCustomerPageAndSort(from, to ,isNonBlock, searchField, keyword, sortType, sortField, size,page));
	}

	@PatchMapping(value = "{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<?> changeInfo(@PathVariable int id, @ModelAttribute ChangeCustomerInfoRequest request){
		if(request.isNull())
			throw new BadRequestException("Content is null");
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return ResponseEntity.ok(customerService.changeInfo(id, email, request));
	}
}
