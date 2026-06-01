package com.ota.travi.service;

import com.ota.travi.entity.ComplaintResolutionAction;
import com.ota.travi.entity.CustomerVoucher;
import com.ota.travi.entity.Voucher;
import com.ota.travi.enums.ComplaintResolutionActionType;
import com.ota.travi.enums.SourceTypeVoucher;
import com.ota.travi.enums.TrangThaiCustomerVoucher;
import com.ota.travi.enums.TrangThaiUuDai;
import com.ota.travi.exception.ResourceNotFoundException;
import com.ota.travi.repository.CustomerVoucherRepository;
import com.ota.travi.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ComplaintCompensationVoucherService {

    private final CustomerVoucherRepository customerVoucherRepository;
    private final VoucherRepository voucherRepository;
    private final NotificationEventService notificationEventService;

    private static final Double DEFAULT_VOUCHER_VALUE = 500000.0;
    private static final Integer DEFAULT_DISCOUNT_PERCENT = 10;
    private static final Integer VOUCHER_VALIDITY_DAYS = 30;

    /**
     * Create a voucher from complaint compensation
     * Called when complaint resolution action is VOUCHER or DISCOUNT_CODE
     */
    @Transactional
    public CustomerVoucher createVoucherFromCompensation(
            String complaintId,
            String customerId,
            ComplaintResolutionAction compensationAction) {
        
        log.info("Creating voucher from complaint {} for customer {}", complaintId, customerId);

        Voucher voucher;
        
        if (compensationAction.getActionType() == ComplaintResolutionActionType.VOUCHER) {
            // Create fixed amount voucher (500k default)
            voucher = createFixedAmountVoucher(compensationAction);
        } else if (compensationAction.getActionType() == ComplaintResolutionActionType.DISCOUNT_CODE) {
            // Create percentage discount voucher (10% default)
            voucher = createPercentageVoucher(compensationAction);
        } else {
            throw new IllegalArgumentException("Invalid compensation action type: " + compensationAction.getActionType());
        }

        voucherRepository.save(voucher);
        log.info("Voucher created with ID: {}", voucher.getId());

        // Create customer voucher entry
        CustomerVoucher customerVoucher = new CustomerVoucher();
        customerVoucher.setCustomerId(customerId);
        customerVoucher.setVoucherId(voucher.getId());
        customerVoucher.setMaVoucherCaNhan(generateUniqueVoucherCode(customerId));
        customerVoucher.setTrangThai(TrangThaiCustomerVoucher.CHUA_DUNG);
        customerVoucher.setSourceType(SourceTypeVoucher.COMPLAINT_COMPENSATION);
        customerVoucher.setIssuedAt(LocalDateTime.now());
        customerVoucher.setExpiredAt(LocalDateTime.now().plusDays(VOUCHER_VALIDITY_DAYS));

        CustomerVoucher savedCustomerVoucher = customerVoucherRepository.save(customerVoucher);
        log.info("Customer voucher created for customer: {} with personal code: {}", customerId, customerVoucher.getMaVoucherCaNhan());

        // Emit event
        notificationEventService.emitVoucherReceived(
                customerId,
                voucher.getId(),
                SourceTypeVoucher.COMPLAINT_COMPENSATION.name()
        );

        return savedCustomerVoucher;
    }

    /**
     * Handle complaint resolution voucher integration point
     * Called from complaint resolution workflow
     */
    @Transactional
    public void handleComplaintResolutionVoucher(
            ComplaintResolutionAction action,
            String customerId,
            String complaintId) {
        
        log.info("Handling complaint resolution voucher for customer: {} from complaint: {}", customerId, complaintId);

        if (action.getActionType() == ComplaintResolutionActionType.VOUCHER || 
            action.getActionType() == ComplaintResolutionActionType.DISCOUNT_CODE) {
            createVoucherFromCompensation(complaintId, customerId, action);
            log.info("Voucher successfully created as complaint compensation");
        }
    }

    /**
     * Create a fixed amount voucher
     */
    private Voucher createFixedAmountVoucher(ComplaintResolutionAction action) {
        Voucher voucher = new Voucher();
        
        Double voucherAmount = DEFAULT_VOUCHER_VALUE;
        if (action.getAmount() != null) {
            voucherAmount = action.getAmount().doubleValue();
        }

        voucher.setTenUuDai("Compensation - Complaint #" + action.getComplaint().getId());
        voucher.setMoTa("Voucher issued as compensation for complaint resolution");
        voucher.setMaVoucher("COMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        voucher.setSoLuongPhatHanh(1);
        voucher.setUsageLimitPerUser(1);
        voucher.setLoaiGiamGia(com.ota.travi.enums.LoaiGiamGia.SO_TIEN_CO_DINH);
        voucher.setMucGiam(voucherAmount);
        voucher.setTrangThaiUuDai(TrangThaiUuDai.DANG_CO_HIEU_LUC);
        voucher.setNgayBatDau(java.time.LocalDate.now());
        voucher.setNgayKetThuc(java.time.LocalDate.now().plusDays(VOUCHER_VALIDITY_DAYS));

        return voucher;
    }

    /**
     * Create a percentage discount voucher
     */
    private Voucher createPercentageVoucher(ComplaintResolutionAction action) {
        Voucher voucher = new Voucher();
        
        Integer discountPercent = DEFAULT_DISCOUNT_PERCENT;
        if (action.getDiscountPercent() != null) {
            discountPercent = action.getDiscountPercent();
        }

        voucher.setTenUuDai("Discount Code - Complaint #" + action.getComplaint().getId());
        voucher.setMoTa("Discount code issued as compensation for complaint resolution");
        voucher.setMaVoucher("DISC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        voucher.setSoLuongPhatHanh(1);
        voucher.setUsageLimitPerUser(1);
        voucher.setLoaiGiamGia(com.ota.travi.enums.LoaiGiamGia.PHAN_TRAM);
        voucher.setMucGiam(discountPercent.doubleValue());
        voucher.setGiaTriGiamToiDa(500000.0);
        voucher.setTrangThaiUuDai(TrangThaiUuDai.DANG_CO_HIEU_LUC);
        voucher.setNgayBatDau(java.time.LocalDate.now());
        voucher.setNgayKetThuc(java.time.LocalDate.now().plusDays(VOUCHER_VALIDITY_DAYS));

        return voucher;
    }

    /**
     * Generate unique voucher code for customer
     */
    private String generateUniqueVoucherCode(String customerId) {
        return "CV-" + customerId.substring(0, Math.min(6, customerId.length())).toUpperCase() + 
               "-" + System.currentTimeMillis() % 1000000;
    }
}
