package com.support.desk.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.support.desk.dto.TicketCommentDTO;
import com.support.desk.dto.TicketDTO;
import com.support.desk.dto.TicketDetailsUpdateDTO;
import com.support.desk.dto.TicketEmpDTO;
import com.support.desk.exception.TicketNotFoundException;
import com.support.desk.exception.UserNotFoundException;
import com.support.desk.model.Ticket;
import com.support.desk.model.TicketComment;
import com.support.desk.model.TicketPriority;
import com.support.desk.model.TicketStatus;
import com.support.desk.model.User;
import com.support.desk.repository.TicketCommentRepository;
import com.support.desk.repository.TicketRepository;
import com.support.desk.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class TicketService {

	private static final Logger logger = LogManager.getLogger(TicketService.class);

	@Autowired
	private TicketRepository ticketRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TicketCommentRepository ticketCommentRepository;
    
	@Transactional
	public TicketDTO createTicket(TicketDTO ticketDTO,Long userId) {
	Optional<User> userOpt = userRepository.findById(userId);
	  if (userOpt.isEmpty()) {
		  logger.warn("User not found with id: {}", userId);
		throw new UserNotFoundException("User not found with id: " + userId);
	}
	User customer = userOpt.get();
	Ticket ticket = new Ticket();
	ticket.setTicketId(generateFourDigitNumber());
	ticket.setTitle(ticketDTO.getTitle());
	ticket.setDescription(ticketDTO.getDescription());
	ticket.setPriority(TicketPriority.LOW);
	ticket.setStatus(TicketStatus.OPEN);
	ticket.setCustomer(customer);
	ticket.setCreationTime(LocalDateTime.now());
	ticket.setResolutionTime(LocalDateTime.now().plusHours(48));
	Ticket savedTicket = ticketRepository.save(ticket);
	  logger.info("Creating ticket: {} for user: {}", savedTicket.getTicketId(), customer.getEmail());
	return convertToDTO(savedTicket);
	}
    
	@Transactional
	public TicketDTO assignTicket(Long ticketId, Long agentId) {
	Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
	  if (ticketOpt.isEmpty()) {
		  logger.warn("Ticket not found with id: {}", ticketId);
		throw new TicketNotFoundException("Ticket not found with id: " + ticketId);
	}
	Ticket ticket = ticketOpt.get();
	Optional<User> agentOpt = userRepository.findById(agentId);
	  if (agentOpt.isEmpty()) {
		  logger.warn("Agent not found with id: {}", agentId);
		throw new UserNotFoundException("Agent not found with id: " + agentId);
	}
	User agent = agentOpt.get();
	ticket.setAssignedAgent(agent);
	ticket.setDepartment(agent.getDepartment());
	Ticket updatedTicket = ticketRepository.save(ticket);
	  logger.info("Assigned ticket: {} to agent: {}", updatedTicket.getTicketId(), agent.getEmail());
	return convertToDTO(updatedTicket);
	}
    
	@Transactional
	public String updateTicket(TicketDetailsUpdateDTO ticketDetailsUpdateDTO) {
	 Ticket ticket =
	ticketRepository.findByTicketId(ticketDetailsUpdateDTO.getTicketId());
	 if (ticket == null) {
	 	 logger.warn("Ticket not found with id: {}", ticketDetailsUpdateDTO.getTicketId());
		 throw new TicketNotFoundException("Ticket not found with id: "
		 + ticketDetailsUpdateDTO.getTicketId());
		 }
	 if(!ticket.getStatus().equals(TicketStatus.RESOLVED)){
	 if(ticketDetailsUpdateDTO.getStatus()!=ticket.getStatus() &&
	ticketDetailsUpdateDTO.getStatus()!=null){
	 ticket.setStatus(ticketDetailsUpdateDTO.getStatus());
	 if(ticket.getStatus().equals(TicketStatus.RESOLVED)){
	 ticket.setResolutionTime(LocalDateTime.now());
	 }
	 }
	 if (!ticketDetailsUpdateDTO.getContent().isEmpty()){
	 TicketComment ticketComment = new TicketComment();
	 ticketComment.setTicket(ticket);
	 ticketComment.setUser(ticket.getCustomer());
	 ticketComment.setContent(ticketDetailsUpdateDTO.getContent());
	 ticketComment.setCreatedAt(LocalDateTime.now());
	 ticket.getComments().add(ticketComment);
	 }
	 if (ticketDetailsUpdateDTO.getPriority()!=ticket.getPriority() &&
	ticketDetailsUpdateDTO.getPriority()!=null){
	 ticket.setPriority(ticketDetailsUpdateDTO.getPriority());
	 }
	 	 ticketRepository.save(ticket);
	 	 logger.info("Updated ticket: {} status: {}", ticket.getTicketId(), ticket.getStatus());
	 return "Ticket details updated successfully";
	 }
	 else{
	 	 logger.warn("Attempt to update already resolved ticket: {}", ticket.getTicketId());
	 return "The Ticket with id "+ ticket.getTicketId()+" is already Resolved.";
	 }
	}
    
	@Transactional
	public List<TicketDTO> getTicketsAssociatedToCustomer(Long userId) {
	Optional<User> customerOpt = userRepository.findById(userId);
	 	 if (customerOpt.isEmpty()){
	 	 	logger.warn("User not found with id when fetching tickets: {}", userId);
		throw new UserNotFoundException("User not found with username: ");
	}
	User customer = customerOpt.get();
	List<Ticket> tickets = ticketRepository.findByCustomer(customer);
	 	 if (tickets.isEmpty()) {
	 	 	logger.warn("No tickets found for user with id: {}", userId);
		throw new TicketNotFoundException("No tickets found for user with id: " + userId);
		}
	 	logger.info("Found {} tickets for user: {}", tickets.size(), customer.getEmail());
	return
	tickets.stream().map(this::convertToDTO).collect(Collectors.toList());
	}
    
	@Transactional
	public List<TicketEmpDTO> getTicketsByAgent(Long userId) {
	Optional<User> agentOpt = userRepository.findById(userId);
	 	 if (agentOpt.isEmpty()){
	 	 	logger.warn("Agent not found with id when fetching tickets: {}", userId);
		throw new UserNotFoundException("User not found with id: " + userId);
	}
	List<Ticket> tickets = ticketRepository.findByAssignedAgent(agentOpt.get());
	 	 if (tickets.isEmpty()) {
	 	 	logger.warn("No tickets found assigned to agent with id: {}", userId);
		throw new TicketNotFoundException("No tickets found assigned to agent with id: " + userId);
		}
	 	logger.info("Found {} tickets assigned to agent id: {}", tickets.size(), userId);
	return tickets.stream().map(this::convertToDTOs).collect(Collectors.
	toList());
	}
    
	@Transactional
	public List<TicketDTO> getTicketsByStatus(TicketStatus status) {
	List<Ticket> tickets = ticketRepository.findByStatus(status);
	 	 if (tickets.isEmpty()) {
	 	 	logger.warn("No tickets found with status: {}", status);
		throw new TicketNotFoundException("No tickets found with status: " + status);
		}
	 	logger.info("Found {} tickets with status: {}", tickets.size(), status);
	return tickets.stream().map(this::convertToDTO).collect(
	Collectors.toList());
	}
    
	@Transactional
	public List<TicketDTO> getTicketsByDepartment(String department) {
	 List<Ticket> tickets = ticketRepository.findByDepartment(department);
	 	 if (tickets.isEmpty()) {
	 	 	logger.warn("No tickets found in department: {}", department);
		 throw new TicketNotFoundException("No tickets found in department: " + department);
		 }
	 	logger.info("Found {} tickets in department: {}", tickets.size(), department);
	 return tickets.stream()
	 .map(this::convertToDTO)
	 .collect(Collectors.toList());
	}
    
	@Transactional
	public Long getTotalActiveTicketCount() {
	Integer size = ticketRepository.findByStatus(TicketStatus.OPEN).size();
	 	logger.info("Total active tickets: {}", size);
	return size.longValue();
	}
    
	@Transactional
	public List<TicketCommentDTO> getCommentsByTicket(Long ticketId) {
	Ticket ticket = ticketRepository.findByTicketId(ticketId);
	 	if (ticket == null){
	 		logger.warn("Ticket not found when fetching comments: {}", ticketId);
		throw new TicketNotFoundException("Ticket not found with id: " + ticketId);
	}
	List<TicketComment> comments = ticketCommentRepository
	.findByTicketOrderByCreatedAtAsc(ticket);
	 	logger.info("Found {} comments for ticket: {}", comments.size(), ticketId);
	return comments.stream()
	.map(this::convertToCommentDTO).collect(Collectors.toList());
	}
    
	private TicketDTO convertToDTO(Ticket ticket) {
		TicketDTO dto = new TicketDTO();
		dto.setTicketId(ticket.getTicketId());
		dto.setTitle(ticket.getTitle());
		dto.setDescription(ticket.getDescription());
		dto.setCreationTime(ticket.getCreationTime());
		dto.setResolutionTime(ticket.getResolutionTime());
		dto.setAssignedAgent(ticket.getAssignedAgent());
		dto.setStatus(ticket.getStatus());
		dto.setComments(ticket.getComments());
		return dto;
		}
    
	private TicketEmpDTO convertToDTOs(Ticket ticket) {
		TicketEmpDTO dto = new TicketEmpDTO();
		dto.setTicketId(ticket.getTicketId());
		dto.setTitle(ticket.getTitle());
		dto.setDescription(ticket.getDescription());
		dto.setCreationTime(ticket.getCreationTime());
		dto.setResolutionTime(ticket.getResolutionTime());
		dto.setPriority(ticket.getPriority());
		dto.setStatus(ticket.getStatus());
		dto.setCustomer(ticket.getCustomer());
		dto.setComments(ticket.getComments());
		return dto;
		}
    
	private TicketCommentDTO convertToCommentDTO(TicketComment comment) {
		TicketCommentDTO dto = new TicketCommentDTO();
		dto.setContent(comment.getContent());
		dto.setCreatedAt(comment.getCreatedAt());
		return dto;
		}
    
	public static Long generateFourDigitNumber() {
		Random random = new Random();
		// generates a number between 1000 and 9999
		return 1000 + random.nextLong(9000);
		}


}
