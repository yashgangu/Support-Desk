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
import org.springframework.web.bind.annotation.RestController;

import com.support.desk.dto.TicketDetailsUpdateDTO;
import com.support.desk.dto.TicketEmpDTO;
import com.support.desk.dto.UserRegistrationDTO;
import com.support.desk.service.EmployeeService;
import com.support.desk.service.TicketService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employee")

public class EmployeeController {
	
	@Autowired
	private TicketService ticketService;
	@Autowired
	private EmployeeService employeeService;


	@PostMapping("/register/employee")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public ResponseEntity<?> registerEmployee(
	@Valid @RequestBody UserRegistrationDTO registrationDto) {
		employeeService.registerEmployee(registrationDto);
	return ResponseEntity.ok()
	.body("Employee registered successfully!");
	}
	@GetMapping("/tickets/{userId}")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public ResponseEntity<List<TicketEmpDTO>> getAssignedTickets(
	@PathVariable Long userId) {
	return ResponseEntity.ok(ticketService.getTicketsByAgent(userId));
	}
	@PutMapping("/ticket/update")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public String updateTicket(
	@RequestBody TicketDetailsUpdateDTO ticketDetailsUpdateDTO) {
	return ticketService.updateTicket(ticketDetailsUpdateDTO);
	}

}
