package com.support.desk.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.support.desk.model.Ticket;
import com.support.desk.model.TicketComment;

@Repository
public interface TicketCommentRepository extends JpaRepository<TicketComment, Long>{

	List<TicketComment> findByTicketOrderByCreatedAtAsc(Ticket ticket);
}
