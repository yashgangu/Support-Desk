package com.support.desk.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.support.desk.dto.TicketDTO;
import com.support.desk.dto.TicketEmpDTO;
import com.support.desk.dto.UserRegistrationDTO;
import com.support.desk.model.TicketStatus;
import com.support.desk.service.AdminService;
import com.support.desk.service.TicketService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

	@Autowired
	private TicketService ticketService;
	@Autowired
	private AdminService adminService;
	@PostMapping("/register/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> registerAdmin(
	@Valid @RequestBody UserRegistrationDTO registrationDto) {
	adminService.registerAdmin(registrationDto);
	return ResponseEntity.ok().body("Admin registered successfully!");
	}
	@GetMapping("/tickets")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<TicketDTO>> getAllOpenTickets() {
	return ResponseEntity.ok(
	ticketService.getTicketsByStatus(TicketStatus.OPEN));
	}
	@GetMapping("/tickets/status/{status}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<TicketDTO>> getTicketsByStatus(
	@PathVariable String status) {
	TicketStatus ticketStatus = TicketStatus.valueOf(status);
	return ResponseEntity.ok(
	ticketService.getTicketsByStatus(ticketStatus));
	}
	@GetMapping("/tickets/department/{department}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<TicketDTO>> getTicketsByDepartment(
	@PathVariable String department) {
	return ResponseEntity.ok(
	ticketService.getTicketsByDepartment(department));
	}
	@GetMapping("/tickets/agent/{userId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<TicketEmpDTO>> getTicketsByAgent(
	@PathVariable Long userId) {
	return ResponseEntity.ok(ticketService.getTicketsByAgent(userId));
	}
	@GetMapping("/tickets/count")
	@PreAuthorize("hasRole('ADMIN')")
	public Long getTotalActiveTickets() {
	return ticketService.getTotalActiveTicketCount();
	}
	@PutMapping("/ticket/assign")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<TicketDTO> assignTicket(
	@RequestParam Long ticketId, @RequestParam Long userId) {
	return ResponseEntity.ok(
	ticketService.assignTicket(ticketId, userId));
	}

}
