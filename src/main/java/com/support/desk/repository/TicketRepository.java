package com.support.desk.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.support.desk.model.Ticket;
import com.support.desk.model.TicketStatus;
import com.support.desk.model.User;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>{

	List<Ticket> findByCustomer(User customer);
	Ticket findByTicketId(Long ticketId);
	List<Ticket> findByAssignedAgent(User agent);
	List<Ticket> findByStatus(TicketStatus status);
	List<Ticket> findByDepartment(String department);
}
