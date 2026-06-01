package com.ota.travi.repository;

import com.ota.travi.entity.MilestoneProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MilestoneProgressRepository extends JpaRepository<MilestoneProgress, Long> {
    List<MilestoneProgress> findByCustomerId(Long customerId);

    Optional<MilestoneProgress> findByCustomerIdAndMilestone5Granted(Long customerId, boolean granted);
}
