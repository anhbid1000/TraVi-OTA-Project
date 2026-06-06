package com.ota.travi.repository;

import com.ota.travi.entity.ComplaintActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintActivityRepository extends JpaRepository<ComplaintActivity, String> {
    List<ComplaintActivity> findByComplaint_IdOrderByCreatedAtAsc(String complaintId);
}
