package com.support.desk.service;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.support.desk.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{
    
	private static final Logger logger = LogManager.getLogger(CustomUserDetailsService.class);

	@Autowired
	private UserRepository userRepository;
	@Override
	public UserDetails loadUserByUsername(String username) throws
	UsernameNotFoundException {
	Optional<?> userOpt = this.userRepository.findByUsername(username);
	  if (userOpt.isEmpty()){
		logger.warn("User not found by username: {}", username);
		throw new UsernameNotFoundException("User NotFound");
	}
	  logger.info("Loaded user details for: {}", username);
	return (UserDetails) userOpt.get();
	}
}
