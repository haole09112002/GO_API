package com.GOBookingAPI.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.GOBookingAPI.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.services.IDriverService;
import com.GOBookingAPI.services.IStatisticsService;
import com.GOBookingAPI.services.IUserService;
import com.google.firebase.database.annotations.Nullable;

import jakarta.websocket.server.PathParam;
import net.minidev.json.JSONObject;


@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private IStatisticsService statisticsService;
	
	@GetMapping("/statistics")
	@PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getStatisticsDay(@RequestParam(name ="from" , required = false) @Nullable @DateTimeFormat(pattern =  "yyyy-MM-dd") Date from,
  											@RequestParam(name = "to"  , required = false)@Nullable @DateTimeFormat(pattern =  "yyyy-MM-dd")  Date to,
  											@RequestParam(name ="unit" , required = false) String unit
  											){
  	return ResponseEntity.ok(statisticsService.getStatisticsResponse(from, to,  unit));
  }
}
