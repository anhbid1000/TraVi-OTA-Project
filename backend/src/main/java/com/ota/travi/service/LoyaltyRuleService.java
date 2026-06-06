package com.ota.travi.service;

import com.ota.travi.entity.LoyaltyRule;
import com.ota.travi.exception.ResourceNotFoundException;
import com.ota.travi.exception.ValidationException;
import com.ota.travi.repository.LoyaltyRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LoyaltyRuleService {

    private final LoyaltyRuleRepository loyaltyRuleRepository;

    // ========== ACTIVE RULE RETRIEVAL ==========

    @Transactional(readOnly = true)
    public LoyaltyRule requireActiveRule() {
        log.debug("Requiring active loyalty rule");
        
        return loyaltyRuleRepository.findByIsActiveTrue()
                .orElseThrow(() -> {
                    log.warn("No active loyalty rule found");
                    return new ResourceNotFoundException("Không có quy tắc tích lũy điểm nào được kích hoạt");
                });
    }

    @Transactional(readOnly = true)
    public Optional<LoyaltyRule> getActiveRule() {
        log.debug("Fetching active loyalty rule");
        return loyaltyRuleRepository.findByIsActiveTrue();
    }

    // ========== RULE UPDATE ==========

    @Transactional
    public LoyaltyRule updateRule(LoyaltyRule loyaltyRule) {
        log.info("Updating loyalty rule with ID: {}", loyaltyRule.getId());
        
        if (loyaltyRule.getId() == null) {
            throw new ValidationException("ID quy tắc không được để trống");
        }
        
        LoyaltyRule existing = loyaltyRuleRepository.findById(loyaltyRule.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Quy tắc tích lũy điểm không tìm thấy"));
        
        validateRuleData(loyaltyRule);
        
        if (loyaltyRule.getIsActive() != null && loyaltyRule.getIsActive()) {
            deactivateAllOtherRules();
        }
        
        existing.setMoneyPerPoint(loyaltyRule.getMoneyPerPoint());
        existing.setSilverThreshold(loyaltyRule.getSilverThreshold());
        existing.setGoldThreshold(loyaltyRule.getGoldThreshold());
        existing.setDiamondThreshold(loyaltyRule.getDiamondThreshold());
        existing.setSilverMultiplier(loyaltyRule.getSilverMultiplier());
        existing.setGoldMultiplier(loyaltyRule.getGoldMultiplier());
        existing.setDiamondMultiplier(loyaltyRule.getDiamondMultiplier());
        
        if (loyaltyRule.getIsActive() != null) {
            existing.setIsActive(loyaltyRule.getIsActive());
        }
        
        LoyaltyRule saved = loyaltyRuleRepository.save(existing);
        log.info("Loyalty rule updated successfully with ID: {}", saved.getId());
        return saved;
    }

    // ========== VALIDATION ==========

    private void validateRuleData(LoyaltyRule rule) {
        if (rule.getMoneyPerPoint() == null || rule.getMoneyPerPoint() <= 0) {
            throw new ValidationException("Tiền cho mỗi điểm phải lớn hơn 0");
        }
        
        if (rule.getSilverThreshold() == null || rule.getSilverThreshold() < 0) {
            throw new ValidationException("Ngưỡng bạc không được để trống và phải >= 0");
        }
        
        if (rule.getGoldThreshold() == null || rule.getGoldThreshold() <= rule.getSilverThreshold()) {
            throw new ValidationException("Ngưỡng vàng phải lớn hơn ngưỡng bạc");
        }
        
        if (rule.getDiamondThreshold() == null || rule.getDiamondThreshold() <= rule.getGoldThreshold()) {
            throw new ValidationException("Ngưỡng kim cương phải lớn hơn ngưỡng vàng");
        }
        
        if (rule.getSilverMultiplier() == null || rule.getSilverMultiplier() <= 0) {
            throw new ValidationException("Hệ số bạc phải lớn hơn 0");
        }
        
        if (rule.getGoldMultiplier() == null || rule.getGoldMultiplier() <= 0) {
            throw new ValidationException("Hệ số vàng phải lớn hơn 0");
        }
        
        if (rule.getDiamondMultiplier() == null || rule.getDiamondMultiplier() <= 0) {
            throw new ValidationException("Hệ số kim cương phải lớn hơn 0");
        }
    }

    private void deactivateAllOtherRules() {
        log.debug("Deactivating all other loyalty rules");
        
        loyaltyRuleRepository.findAll().forEach(rule -> {
            if (rule.getIsActive()) {
                rule.setIsActive(false);
                loyaltyRuleRepository.save(rule);
            }
        });
    }
}
