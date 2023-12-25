package com.GOBookingAPI.controller;


import com.GOBookingAPI.payload.request.DriverStatusRequest;
import com.GOBookingAPI.services.IDriverService;
import com.GOBookingAPI.utils.AppConstants;
import com.GOBookingAPI.utils.DriverStatus;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@Validated
@RequestMapping("/drivers")
public class DriverController {

	@Autowired
	private IDriverService driverService;

	@GetMapping
	@PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
	public ResponseEntity<?> getDriverInfo(@RequestParam (required = false, defaultValue = "-1" ) Integer id) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return ResponseEntity.ok(driverService.getDriverInfo(email, id));
	}

	@GetMapping("/{id}/base-profile")
	public ResponseEntity<?> getDriverBaseInfo(@PathVariable Integer id) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return ResponseEntity.ok(driverService.getDriverBaseInfo(email, id));
	}

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> changeDriverStatus(@PathVariable Integer id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(driverService.changeDriverStatus(email, id));
    }

	@GetMapping("/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> getDrivers(@RequestParam(required = false) @Nullable @DateTimeFormat( pattern = "yyyy-MM-dd") Date from,
									  @RequestParam(required = false) @Nullable @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
									  @RequestParam(required = false ,defaultValue = AppConstants.IS_NON_BLOCK ) boolean isNonBlock,
									  @RequestParam(required = false) DriverStatus status,
									  @RequestParam(required = false) String searchField,
									  @RequestParam(required = false) String keyword,
									  @RequestParam(required = false) String sortType,
									  @RequestParam(required = false) String sortField,
									  @RequestParam(required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
									  @RequestParam(required = false , defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page){
		return ResponseEntity.ok(driverService.getDriverPageAndSort(from, to ,isNonBlock,status, searchField, keyword, sortType, sortField, size,page));
	}

	@PutMapping("/active/{ids}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> activeDriver(@PathVariable String ids){
		return ResponseEntity.ok(driverService.ActiveOrRefuseDriver(ids,AppConstants.ACTIVE.toString()));
	}

	@PutMapping("/refuse/{ids}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> refuseDriver(@PathVariable String ids){
		return ResponseEntity.ok(driverService.ActiveOrRefuseDriver(ids,AppConstants.REFUSE.toString()));
	}
	
	@PutMapping("/block/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> blockDriver(@PathVariable int id, @RequestParam("isBlock") Boolean isBlock){
		return ResponseEntity.ok(driverService.blockStatus(id,isBlock));
	}

	@GetMapping(value = {"/statistics", "/{id}/statistics"})
	@PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
	public ResponseEntity<?> statisticsBooking(@PathVariable(required = false) Integer id, @RequestParam @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") Date from, @RequestParam @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return ResponseEntity.ok(driverService.bookingStatisticByDriver(email, from, to, id));
	}
	
}
