package com.GOBookingAPI.services.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.GOBookingAPI.entities.Provider;
import com.GOBookingAPI.entities.Role;
import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.exceptions.BaseException;
import com.GOBookingAPI.payload.request.UserDTO;
import com.GOBookingAPI.payload.response.BaseResponseDTO;
import com.GOBookingAPI.repositories.RoleRepository;
import com.GOBookingAPI.repositories.UserRepository;
import com.GOBookingAPI.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserSereviceImpl implements UserService{
	
	private final UserRepository userRepository ;
	
	private final RoleRepository roleRepository ;

	private final BCryptPasswordEncoder passwordEncoder;
	
	@Override
	public BaseResponseDTO registerAccount(UserDTO userDTO) {
		
		BaseResponseDTO response = new BaseResponseDTO();
		
		validateAccount(userDTO);
		
		User user = insertUser(userDTO);
		try {
			userRepository.save(user);
			response.setCode(String.valueOf(HttpStatus.OK.value()));
			response.setMessage("Create account successfully");
		}catch(Exception e) {
			response.setCode(String.valueOf(HttpStatus.SERVICE_UNAVAILABLE.value()));
			response.setMessage("Service unavailable");
		}
		
		return response;
	}
	
	private User insertUser(UserDTO userDTO) {
		User user = new User();
		user.setUsername(userDTO.getUsername());
		user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		user.setEmail(userDTO.getEmail());
		user.setPhoneNumber(userDTO.getPhoneNumber());
		user.setIsNonBlock(false);
		user.setAvatarUrl("sadas");
		Set<Role> roles = new HashSet<>();
		user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        
        user.setProviderId(Provider.local.name());
        
		roles.add(roleRepository.findByName(userDTO.getRole()));
		user.setRoles(roles);
		return user;
	}

	
	private void validateAccount(UserDTO userDTO) {
		if(ObjectUtils.isEmpty(userDTO)) {
			throw new BaseException(String.valueOf(HttpStatus.BAD_REQUEST.value()),"Data must not empty");
		}
		 try {
	            if(!ObjectUtils.isEmpty(userDTO.checkProperties())){
	                throw new BaseException(String.valueOf(HttpStatus.BAD_REQUEST.value()), "Request data not found!");
	            }
	        }catch (IllegalAccessException e){
	            throw new BaseException(String.valueOf(HttpStatus.SERVICE_UNAVAILABLE.value()), "Service Unavailable");
	        }
		 List<String> roles = roleRepository.findAll().stream().map(Role::getName).toList();
		 if(!roles.contains(userDTO.getRole())) {
				throw new BaseException(String.valueOf(HttpStatus.BAD_REQUEST.value()),"Invalid role");
			}
		User user = userRepository.findByUsername(userDTO.getUsername());
		if(!ObjectUtils.isEmpty(user)) {
			throw new BaseException(String.valueOf(HttpStatus.BAD_REQUEST.value()),"Data had existed");
		}
		
		
		
	}
}
