package com.support.desk.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.support.desk.jwt.JwtAuthenticationHelper;

@Service
public class AuthService {

	private static final Logger logger = LogManager.getLogger(AuthService.class);

	@Autowired
	AuthenticationManager manager;
	@Autowired
	private JwtAuthenticationHelper jwtHelper;
	@Autowired
	UserDetailsService userDetailsService;
    
	public JwtResponse login(JwtRequest jwtRequest) {
	//authenticate with the Authentication manager
	this.doAuthenticate(jwtRequest.getUsername(),
	jwtRequest.getPassword());
	UserDetails userDetails =
	userDetailsService.loadUserByUsername(jwtRequest.getUsername());
	  String token = jwtHelper.generateToken(userDetails);
	  logger.info("User logged in: {}", jwtRequest.getUsername());
	return JwtResponse.builder().jwtToken(token).build();
	}
	private void doAuthenticate(String username, String password) {
	UsernamePasswordAuthenticationToken authenticationToken =
	new UsernamePasswordAuthenticationToken(username, password);
	try {
	manager.authenticate(authenticationToken);
	  } catch (BadCredentialsException e) {
	  logger.error("Authentication failed for user: {}", username, e);
	throw new BadCredentialsException("Invalid Username or Password");
	}
	}

}
