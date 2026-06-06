package com.ota.travi.repository;

import com.ota.travi.entity.ComplaintMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintMessageRepository extends JpaRepository<ComplaintMessage, String> {
    List<ComplaintMessage> findByComplaint_IdOrderByCreatedAtAsc(String complaintId);
}
