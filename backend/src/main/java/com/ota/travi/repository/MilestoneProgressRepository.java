package com.ota.travi.repository;

import com.ota.travi.entity.MilestoneProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MilestoneProgressRepository extends JpaRepository<MilestoneProgress, Long> {
    
    @Query("""
            SELECT mp FROM MilestoneProgress mp
            WHERE mp.customerId = :customerId
            """)
    Optional<MilestoneProgress> findByCustomerId(@Param("customerId") String customerId);

    Optional<MilestoneProgress> findByCustomerIdAndMilestone5Granted(String customerId, boolean granted);
}
