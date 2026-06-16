package com.support.desk.jwt;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{

	@Autowired
	private JwtAuthenticationHelper jwtHelper;
	@Autowired
	UserDetailsService userDetailsService;
	@Override
	protected void doFilterInternal(
	HttpServletRequest request,
	HttpServletResponse response,
	FilterChain filterChain) throws ServletException, IOException, java.io.IOException {
	String requestHeader = request.getHeader("Authorization");
	String username = null;
	String token = null;
	if (requestHeader != null && requestHeader.startsWith("Bearer")) {
	token = requestHeader.substring(7);
	username = jwtHelper.getUsernameFromToken(token);
	if (username != null &&
	SecurityContextHolder.getContext().getAuthentication() == null) {
	UserDetails userDetails =
	userDetailsService.loadUserByUsername(username);
	if (!jwtHelper.isTokenExpired(token)) {
	UsernamePasswordAuthenticationToken
	usernamePasswordAuthenticationToken
	= new UsernamePasswordAuthenticationToken(token,
	null, userDetails.getAuthorities());
	usernamePasswordAuthenticationToken.setDetails(new
	WebAuthenticationDetailsSource().buildDetails(request));
	SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
	}
	}
	}
	filterChain.doFilter(request, response);
	}

	// Skip this filter for public endpoints (registration, auth endpoints)
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getServletPath();
		if (Objects.equals(path, "/api/customer/register/customer") || path.startsWith("/api/auth")) {
			return true;
		}
		return false;
	}

}
