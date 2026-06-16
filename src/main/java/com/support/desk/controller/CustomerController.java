package com.support.desk.controller;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.support.desk.dto.TicketCommentDTO;
import com.support.desk.dto.TicketDTO;
import com.support.desk.dto.UserRegistrationDTO;
import com.support.desk.service.TicketService;
import com.support.desk.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {
	
	@Autowired
	private TicketService ticketService;
	@Autowired
	private UserService userService;

	@PostMapping("/register/customer")
	public ResponseEntity<?> registerCustomer(
	@Valid @RequestBody UserRegistrationDTO registrationDto) {
	// register as a CUSTOMER so the new user receives the CUSTOMER role
	userService.registerUser(registrationDto, "ROLE_CUSTOMER");
	return ResponseEntity.ok().body("User registered successfully!");
	}
	@PreAuthorize("hasRole('CUSTOMER')")
	@PostMapping("/raise/issue")
	public ResponseEntity<TicketDTO> generateIssue(
	@Valid @RequestBody TicketDTO ticketDTO,
	@RequestParam Long userId) {
	return ResponseEntity.ok(
	ticketService.createTicket(ticketDTO,userId));
	}
	@PreAuthorize("hasRole('CUSTOMER')")
	@GetMapping("/tickets/{userId}")
	public ResponseEntity<List<TicketDTO>> getMyTickets(
	@PathVariable Long userId) {
	return ResponseEntity.ok(
	ticketService.getTicketsAssociatedToCustomer(userId));
	}
	@PreAuthorize("hasRole('CUSTOMER')")
	@GetMapping("/comments/{ticketId}")
	public ResponseEntity<List<TicketCommentDTO>> getComments(
	@PathVariable Long ticketId) {
	return ResponseEntity.ok(
	ticketService.getCommentsByTicket(ticketId));
	}
}
