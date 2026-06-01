package com.ota.travi.repository;

import com.ota.travi.entity.NotificationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationEventRepository extends JpaRepository<NotificationEvent, Long> {
    
    @Query("""
            SELECT ne FROM NotificationEvent ne
            WHERE ne.customerId = :customerId
            AND ne.isProcessed = false
            ORDER BY ne.createdAt DESC
            """)
    List<NotificationEvent> findUnreadEvents(@Param("customerId") String customerId);

    @Query("""
            SELECT ne FROM NotificationEvent ne
            WHERE ne.customerId = :customerId
            ORDER BY ne.createdAt DESC
            """)
    List<NotificationEvent> findByCustomerId(@Param("customerId") String customerId);

    @Query("""
            SELECT ne FROM NotificationEvent ne
            WHERE ne.eventType = :eventType
            AND ne.isProcessed = false
            """)
    List<NotificationEvent> findByEventTypeAndUnprocessed(@Param("eventType") String eventType);
}
