package com.ostafon.supportportal.chat.repo;

import com.ostafon.supportportal.tickets.model.TicketMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ticket chat messages
 */
@Repository
public interface TicketMessageRepo extends JpaRepository<TicketMessageEntity, Long> {

    List<TicketMessageEntity> findByTicket_IdOrderByCreatedAtAsc(Long ticketId);

    long countByTicket_Id(Long ticketId);

    Optional<TicketMessageEntity> findTop1ByTicket_IdOrderByCreatedAtDesc(Long ticketId);
}
