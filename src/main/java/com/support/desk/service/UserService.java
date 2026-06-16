package com.support.desk.service;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.support.desk.dto.UserRegistrationDTO;
import com.support.desk.model.Role;
import com.support.desk.model.User;
import com.support.desk.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {

	private static final Logger logger = LogManager.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepository;
    
	@Autowired
	private PasswordEncoder passwordEncoder;
    
    
    
	@Transactional
	public User registerUser(UserRegistrationDTO registrationDTO) {
	User user = new User();
	user.setUsername(registrationDTO.getUsername());
	user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
	user.setEmail(registrationDTO.getEmail());
	user.setFullName(registrationDTO.getFullName());
	user.setPhoneNumber(registrationDTO.getPhoneNumber());
	user.setAge(registrationDTO.getAge());
	user.setGender(registrationDTO.getGender());
	User saved = userRepository.save(user);
	logger.info("Creating user: {}", saved.getEmail());
	return saved;
	}
    
	public User registerUser(UserRegistrationDTO registrationDTO, String role){

	validateNewUser(registrationDTO);
	User user = new User();
	user.setUsername(registrationDTO.getUsername());
	user.setPassword(
	passwordEncoder.encode(registrationDTO.getPassword()));
	user.setEmail(registrationDTO.getEmail());
	user.setFullName(registrationDTO.getFullName());
	user.setPhoneNumber(registrationDTO.getPhoneNumber());
	user.setAge(registrationDTO.getAge());
	user.setGender(registrationDTO.getGender());
	if (role.contains("ROLE_EMPLOYEE") || role.contains("ROLE_ADMIN")) {
	user.setEmployeeCode(registrationDTO.getEmployeeCode());
	user.setDepartment(registrationDTO.getDepartment());
	} else {
	user.setEmployeeCode("NA");
	user.setDepartment("NA");
	}
	Set<Role> roles = new HashSet<>();
	Role updatedRoles = new Role();
	updatedRoles.setRoleName(role);
	roles.add(updatedRoles);
	user.setRoles(roles);
	User saved = userRepository.save(user);
	logger.info("Creating user with role: {} - {}", saved.getEmail(), role);
	return saved;
	}

	// Validate that username and email are not already taken
	private void validateNewUser(UserRegistrationDTO registrationDTO) {
		if (registrationDTO == null) {
			logger.error("Registration data is null");
			throw new IllegalArgumentException("Registration data must not be null");
		}
		String username = registrationDTO.getUsername();
		String email = registrationDTO.getEmail();
		if (username != null && userRepository.existsByUsername(username)) {
			logger.warn("Username already exists: {}", username);
			throw new IllegalArgumentException("Username already exists: " + username);
		}
		if (email != null && userRepository.existsByEmail(email)) {
			logger.warn("Email already in use: {}", email);
			throw new IllegalArgumentException("Email already in use: " + email);
		}
	}

}
