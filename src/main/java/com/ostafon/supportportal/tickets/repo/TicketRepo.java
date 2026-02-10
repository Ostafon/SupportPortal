package com.ostafon.supportportal.tickets.repo;

import com.ostafon.supportportal.common.enums.TicketStatus;
import com.ostafon.supportportal.tickets.model.TicketEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Ticket entities
 */
@Repository
public interface TicketRepo extends JpaRepository<TicketEntity, Long> {

    /**
     * Find ticket by ID with requester and assignee fetched
     * @param id ticket ID
     * @return optional ticket entity
     */
    @Query("SELECT t FROM TicketEntity t " +
           "LEFT JOIN FETCH t.requester " +
           "LEFT JOIN FETCH t.assignee " +
           "LEFT JOIN FETCH t.group " +
           "WHERE t.id = :id")
    Optional<TicketEntity> findByIdWithDetails(@Param("id") Long id);

    /**
     * Find all tickets created by a specific user
     * @param requesterId requester user ID
     * @param pageable pagination parameters
     * @return page of tickets
     */
    @Query("SELECT t FROM TicketEntity t " +
           "LEFT JOIN FETCH t.requester " +
           "LEFT JOIN FETCH t.assignee " +
           "WHERE t.requester.id = :requesterId " +
           "ORDER BY t.createdAt DESC")
    Page<TicketEntity> findByRequesterId(@Param("requesterId") Long requesterId, Pageable pageable);

    /**
     * Find all tickets assigned to a specific user
     * @param assigneeId assignee user ID
     * @param pageable pagination parameters
     * @return page of tickets
     */
    @Query("SELECT t FROM TicketEntity t " +
           "LEFT JOIN FETCH t.requester " +
           "LEFT JOIN FETCH t.assignee " +
           "WHERE t.assignee.id = :assigneeId " +
           "ORDER BY t.priority DESC, t.createdAt ASC")
    Page<TicketEntity> findByAssigneeId(@Param("assigneeId") Long assigneeId, Pageable pageable);

    /**
     * Find all tickets by status
     * @param status ticket status
     * @param pageable pagination parameters
     * @return page of tickets
     */
    @Query("SELECT t FROM TicketEntity t " +
           "LEFT JOIN FETCH t.requester " +
           "LEFT JOIN FETCH t.assignee " +
           "WHERE t.status = :status " +
           "ORDER BY t.priority DESC, t.createdAt ASC")
    Page<TicketEntity> findByStatus(@Param("status") TicketStatus status, Pageable pageable);

    /**
     * Find all tickets assigned to a group
     * @param groupId group ID
     * @param pageable pagination parameters
     * @return page of tickets
     */
    @Query("SELECT t FROM TicketEntity t " +
           "LEFT JOIN FETCH t.requester " +
           "LEFT JOIN FETCH t.assignee " +
           "LEFT JOIN FETCH t.group " +
           "WHERE t.group.id = :groupId " +
           "ORDER BY t.priority DESC, t.createdAt ASC")
    Page<TicketEntity> findByGroupId(@Param("groupId") Long groupId, Pageable pageable);

    /**
     * Find all tickets with pagination and details fetched
     * @param pageable pagination parameters
     * @return page of tickets
     */
    @Query("SELECT t FROM TicketEntity t " +
           "LEFT JOIN FETCH t.requester " +
           "LEFT JOIN FETCH t.assignee " +
           "LEFT JOIN FETCH t.group " +
           "ORDER BY t.createdAt DESC")
    Page<TicketEntity> findAllWithDetails(Pageable pageable);

    /**
     * Count tickets by status
     * @param status ticket status
     * @return count of tickets
     */
    long countByStatus(TicketStatus status);

    /**
     * Count tickets assigned to user
     * @param assigneeId assignee user ID
     * @return count of tickets
     */
    long countByAssigneeId(Long assigneeId);

    /**
     * Find unassigned tickets
     * @param pageable pagination parameters
     * @return page of unassigned tickets
     */
    @Query("SELECT t FROM TicketEntity t " +
           "LEFT JOIN FETCH t.requester " +
           "WHERE t.assignee IS NULL " +
           "ORDER BY t.priority DESC, t.createdAt ASC")
    Page<TicketEntity> findUnassignedTickets(Pageable pageable);

    /**
     * Count unassigned tickets
     * @return count of unassigned tickets
     */
    long countByAssigneeIsNull();

    /**
     * Count tickets created after specific date
     * @param date date threshold
     * @return count of tickets
     */
    long countByCreatedAtAfter(LocalDateTime date);

    /**
     * Count tickets by status and updated after specific date
     * @param status ticket status
     * @param date date threshold
     * @return count of tickets
     */
    long countByStatusAndUpdatedAtAfter(TicketStatus status, LocalDateTime date);

    /**
     * Find all tickets by assignee ID (for performance calculation)
     * @param assigneeId assignee user ID
     * @return list of tickets
     */
    List<TicketEntity> findAllByAssigneeId(Long assigneeId);

    /**
     * Find all tickets with specific statuses
     * @param statuses list of statuses
     * @return list of tickets
     */
    List<TicketEntity> findAllByStatusIn(List<TicketStatus> statuses);

    /**
     * Find all tickets created between dates
     * @param startDate start date
     * @param endDate end date
     * @return list of tickets
     */
    List<TicketEntity> findAllByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find all tickets by status in and closed between dates
     * @param statuses list of statuses
     * @param startDate start date
     * @param endDate end date
     * @return list of tickets
     */
    List<TicketEntity> findAllByStatusInAndClosedAtBetween(List<TicketStatus> statuses, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Count tickets created between dates
     * @param startDate start date
     * @param endDate end date
     * @return count
     */
    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find all tickets by assignee and created between dates
     * @param assigneeId assignee ID
     * @param startDate start date
     * @param endDate end date
     * @return list of tickets
     */
    List<TicketEntity> findAllByAssigneeIdAndCreatedAtBetween(Long assigneeId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Count distinct requester IDs in period
     * @param startDate start date
     * @param endDate end date
     * @return count
     */
    @Query("SELECT COUNT(DISTINCT t.requester.id) FROM TicketEntity t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    long countDistinctRequesterIds(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find ticket counts by requester
     * @param startDate start date
     * @param endDate end date
     * @return list of counts
     */
    @Query("SELECT COUNT(t.id) FROM TicketEntity t WHERE t.createdAt BETWEEN :startDate AND :endDate GROUP BY t.requester.id")
    List<Long> findTicketCountsByRequester(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find distinct requester IDs in period
     * @param startDate start date
     * @param endDate end date
     * @return list of user IDs
     */
    @Query("SELECT DISTINCT t.requester.id FROM TicketEntity t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    List<Long> findDistinctRequesterIds(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Count tickets by status not in and created before date
     * @param statuses excluded statuses
     * @param beforeDate before date
     * @return count
     */
    @Query("SELECT COUNT(t.id) FROM TicketEntity t WHERE t.status NOT IN :statuses AND t.createdAt < :beforeDate")
    long countByStatusNotInAndCreatedAtBefore(@Param("statuses") List<TicketStatus> statuses, @Param("beforeDate") LocalDateTime beforeDate);
}

