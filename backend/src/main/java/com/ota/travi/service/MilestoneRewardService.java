package com.ota.travi.service;

import com.ota.travi.entity.CustomerVoucher;
import com.ota.travi.entity.MilestoneProgress;
import com.ota.travi.enums.SourceTypeVoucher;
import com.ota.travi.enums.TrangThaiCustomerVoucher;
import com.ota.travi.exception.BusinessConflictException;
import com.ota.travi.exception.ResourceNotFoundException;
import com.ota.travi.repository.CustomerVoucherRepository;
import com.ota.travi.repository.MilestoneProgressRepository;
import com.ota.travi.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MilestoneRewardService {

    private final MilestoneProgressRepository milestoneProgressRepository;
    private final CustomerVoucherRepository customerVoucherRepository;
    private final VoucherRepository voucherRepository;
    private final NotificationEventService notificationEventService;

    @Value("${milestone.5.points:100}")
    private Integer milestone5Points;

    @Value("${milestone.10.points:500}")
    private Integer milestone10Points;

    @Value("${milestone.20.points:1000}")
    private Integer milestone20Points;

    public void grantMilestoneRewardIfEligible(String customerId, Integer completedBookingsCount) {
        log.info("Checking milestone rewards for customer {} with {} completed bookings", 
                customerId, completedBookingsCount);

        MilestoneProgress progress = getMilestoneProgress(customerId);

        // Check milestone 5
        if (!progress.getMilestone5Granted() && completedBookingsCount >= 5) {
            grantMilestone(progress, 5, milestone5Points, "Milestone 5 Reached");
        }

        // Check milestone 10
        if (!progress.getMilestone10Granted() && completedBookingsCount >= 10) {
            grantMilestone(progress, 10, milestone10Points, "Milestone 10 Reached");
        }

        // Check milestone 20
        if (!progress.getMilestone20Granted() && completedBookingsCount >= 20) {
            grantMilestone(progress, 20, milestone20Points, "Milestone 20 Reached");
        }

        milestoneProgressRepository.save(progress);
    }

    private void grantMilestone(MilestoneProgress progress, Integer milestone, Integer points, String description) {
        String customerId = progress.getCustomerId();
        log.info("Granting milestone {} reward ({} points) to customer {}", milestone, points, customerId);

        // Create customer voucher for the milestone reward
        try {
            com.ota.travi.entity.Voucher voucherEntity = createOrGetMilestoneVoucher(milestone);
            
            CustomerVoucher voucher = new CustomerVoucher();
            voucher.setCustomerId(customerId);
            voucher.setVoucherId(voucherEntity.getId());
            voucher.setTrangThai(TrangThaiCustomerVoucher.CHUA_DUNG);
            voucher.setSourceType(SourceTypeVoucher.MILESTONE_REWARD);
            voucher.setIssuedAt(LocalDateTime.now());
            voucher.setExpiredAt(LocalDateTime.now().plusDays(30));

            customerVoucherRepository.save(voucher);

            // Update milestone progress
            LocalDateTime now = LocalDateTime.now();
            if (milestone == 5) {
                progress.setMilestone5Granted(true);
                progress.setMilestone5GrantedAt(now);
            } else if (milestone == 10) {
                progress.setMilestone10Granted(true);
                progress.setMilestone10GrantedAt(now);
            } else if (milestone == 20) {
                progress.setMilestone20Granted(true);
                progress.setMilestone20GrantedAt(now);
            }

            // Emit event
            notificationEventService.emitMilestoneRewardGranted(customerId, milestone, points);

            log.info("Successfully granted milestone {} reward to customer {}", milestone, customerId);
        } catch (Exception e) {
            log.error("Error granting milestone reward for customer {}: {}", customerId, e.getMessage(), e);
            throw new BusinessConflictException("Failed to grant milestone reward: " + e.getMessage());
        }
    }

    private com.ota.travi.entity.Voucher createOrGetMilestoneVoucher(Integer milestone) {
        String voucherCode = "MILESTONE_" + milestone;
        return voucherRepository.findByMaVoucher(voucherCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Milestone voucher not found: " + voucherCode));
    }

    @Transactional(readOnly = true)
    public MilestoneProgress getMilestoneProgress(String customerId) {
        log.debug("Fetching milestone progress for customer {}", customerId);

        return milestoneProgressRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    log.debug("No milestone progress found for customer {}, creating new", customerId);
                    MilestoneProgress newProgress = new MilestoneProgress();
                    newProgress.setCustomerId(customerId);
                    newProgress.setCompletedBookings(0);
                    newProgress.setMilestone5Granted(false);
                    newProgress.setMilestone10Granted(false);
                    newProgress.setMilestone20Granted(false);
                    return milestoneProgressRepository.save(newProgress);
                });
    }

    @Transactional
    public void incrementBookingCount(String customerId) {
        log.info("Incrementing booking count for customer {}", customerId);

        MilestoneProgress progress = getMilestoneProgress(customerId);
        progress.setCompletedBookings(progress.getCompletedBookings() + 1);
        milestoneProgressRepository.save(progress);

        grantMilestoneRewardIfEligible(customerId, progress.getCompletedBookings());
    }

    @Transactional(readOnly = true)
    public List<MilestoneRewardResponse> getCurrentMilestoneStatus(String customerId) {
        log.debug("Fetching milestone status for customer {}", customerId);

        MilestoneProgress progress = getMilestoneProgress(customerId);
        List<MilestoneRewardResponse> response = new ArrayList<>();

        response.add(MilestoneRewardResponse.builder()
                .milestone(5)
                .isGranted(progress.getMilestone5Granted())
                .rewardPoints(milestone5Points)
                .grantedAt(progress.getMilestone5GrantedAt())
                .build());

        response.add(MilestoneRewardResponse.builder()
                .milestone(10)
                .isGranted(progress.getMilestone10Granted())
                .rewardPoints(milestone10Points)
                .grantedAt(progress.getMilestone10GrantedAt())
                .build());

        response.add(MilestoneRewardResponse.builder()
                .milestone(20)
                .isGranted(progress.getMilestone20Granted())
                .rewardPoints(milestone20Points)
                .grantedAt(progress.getMilestone20GrantedAt())
                .build());

        return response;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class MilestoneRewardResponse {
        private Integer milestone;
        private Boolean isGranted;
        private Integer rewardPoints;
        private LocalDateTime grantedAt;
    }
}

