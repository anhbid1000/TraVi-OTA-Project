package com.ota.travi.repository;

import com.ota.travi.entity.ComplaintResolutionAction;
import com.ota.travi.enums.ComplaintResolutionActionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ComplaintResolutionActionRepository extends JpaRepository<ComplaintResolutionAction, String> {
    List<ComplaintResolutionAction> findByComplaint_IdOrderByCreatedAtAsc(String complaintId);
    boolean existsByComplaint_IdAndStatusIn(String complaintId, Collection<ComplaintResolutionActionStatus> statuses);
    Optional<ComplaintResolutionAction> findByIdAndComplaint_Id(String id, String complaintId);
}
