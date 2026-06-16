package com.support.desk.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
// ...existing imports...
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.support.desk.jwt.JwtAuthenticationFilter;
// ...existing imports...

@Configuration
@EnableWebSecurity
@EnableMethodSecurity

public class SecurityConfig {

	@Autowired
	JwtAuthenticationFilter filter;
	// ...existing fields...
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws
	Exception {
	http.
	csrf(csrf -> csrf.disable())
	.authorizeHttpRequests(auth -> auth
	.requestMatchers("/api/auth/**", "/api/customer/register/customer").permitAll()
	.anyRequest().authenticated()
	)
	.sessionManagement(session -> session
	.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	);
	http.addFilterBefore(filter,
	UsernamePasswordAuthenticationFilter.class);
	return http.build();
	}
	@Bean
	public AuthenticationManager authenticationManager(
	AuthenticationConfiguration builder) throws Exception {
	return builder.getAuthenticationManager();
	}
	@Bean
	public PasswordEncoder passwordEncoder() {
	return new BCryptPasswordEncoder();
	}
	


}
