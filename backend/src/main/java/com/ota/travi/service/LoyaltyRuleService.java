package com.ota.travi.service;

import com.ota.travi.dto.request.LoyaltyRuleRequest;
import com.ota.travi.dto.response.LoyaltyRuleResponse;
import com.ota.travi.entity.LoyaltyRule;
import com.ota.travi.exception.BusinessException;
import com.ota.travi.repository.LoyaltyRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoyaltyRuleService {
    private final LoyaltyRuleRepository loyaltyRuleRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "loyaltyRules", key = "'active_rule'")
    public LoyaltyRule requireActiveRule() {
        return loyaltyRuleRepository.findFirstByIsActiveTrueOrderByIdDesc()
                .orElseThrow(() -> new BusinessException("Chưa cấu hình quy tắc loyalty", HttpStatus.BAD_REQUEST));
    }

    @Transactional
    @CacheEvict(value = "loyaltyRules", key = "'active_rule'")
    public LoyaltyRuleResponse updateLoyaltyRule(LoyaltyRuleRequest request) {
        validateThresholds(request);

        LoyaltyRule rule = loyaltyRuleRepository.findFirstByIsActiveTrueOrderByIdDesc()
                .orElseGet(LoyaltyRule::new);

        rule.setMoneyPerPoint(request.moneyPerPoint());
        rule.setSilverThreshold(request.silverThreshold());
        rule.setGoldThreshold(request.goldThreshold());
        rule.setDiamondThreshold(request.diamondThreshold());
        rule.setIsActive(request.isActive() == null || request.isActive());
        rule.setUpdatedAt(LocalDateTime.now());

        return toResponse(loyaltyRuleRepository.save(rule));
    }

    private void validateThresholds(LoyaltyRuleRequest request) {
        if (!isAscending(request.silverThreshold(), request.goldThreshold(), request.diamondThreshold())) {
            throw new BusinessException(
                    "Ngưỡng hạng thành viên phải tăng dần: bạc <= vàng <= kim cương",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private boolean isAscending(BigDecimal silverThreshold, BigDecimal goldThreshold, BigDecimal diamondThreshold) {
        return silverThreshold.compareTo(goldThreshold) <= 0
                && goldThreshold.compareTo(diamondThreshold) <= 0;
    }

    private LoyaltyRuleResponse toResponse(LoyaltyRule rule) {
        return new LoyaltyRuleResponse(
                rule.getId(),
                rule.getMoneyPerPoint(),
                rule.getSilverThreshold(),
                rule.getGoldThreshold(),
                rule.getDiamondThreshold(),
                rule.getIsActive(),
                rule.getCreatedAt(),
                rule.getUpdatedAt()
        );
    }
}
