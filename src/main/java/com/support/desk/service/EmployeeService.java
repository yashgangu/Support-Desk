package com.support.desk.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.support.desk.dto.UserRegistrationDTO;
import com.support.desk.model.User;
import com.support.desk.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class EmployeeService {

	private static final Logger logger = LogManager.getLogger(EmployeeService.class);

	@Autowired
	private UserRepository userRepository;
	@Transactional
	public User registerEmployee(UserRegistrationDTO registrationDTO) {
	User user = new User();
	user.setUsername(registrationDTO.getUsername());
	user.setPassword(registrationDTO.getPassword());
	user.setEmail(registrationDTO.getEmail());
	user.setFullName(registrationDTO.getFullName());
	user.setPhoneNumber(registrationDTO.getPhoneNumber());
	user.setAge(registrationDTO.getAge());
	user.setGender(registrationDTO.getGender());
	User saved = userRepository.save(user);
	logger.info("Creating employee: {}", saved.getEmail());
	return saved;
	}

}
