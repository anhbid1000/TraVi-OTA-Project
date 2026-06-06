package com.ota.travi.repository;

import com.ota.travi.entity.LoyaltyRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoyaltyRuleRepository extends JpaRepository<LoyaltyRule, Long> {
    Optional<LoyaltyRule> findByIsActiveTrue();
}
