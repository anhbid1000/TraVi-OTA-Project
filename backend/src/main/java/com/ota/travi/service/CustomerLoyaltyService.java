package com.ota.travi.service;

import com.ota.travi.dto.response.CustomerVoucherResponse;
import com.ota.travi.dto.response.ExchangeVoucherResponse;
import com.ota.travi.dto.response.LoyaltySummaryResponse;
import com.ota.travi.dto.response.PointHistoryResponse;
import com.ota.travi.dto.response.PromotionResponse;
import com.ota.travi.entity.CustomerVoucher;
import com.ota.travi.entity.KhachHang;
import com.ota.travi.entity.LichSuDiem;
import com.ota.travi.entity.LoyaltyRule;
import com.ota.travi.entity.Voucher;
import com.ota.travi.enums.HangThanhVien;
import com.ota.travi.enums.LoaiGiaoDichDiem;
import com.ota.travi.enums.SourceTypeVoucher;
import com.ota.travi.enums.TrangThaiCustomerVoucher;
import com.ota.travi.enums.TrangThaiUuDai;
import com.ota.travi.exception.ForbiddenOperationException;
import com.ota.travi.exception.ResourceNotFoundException;
import com.ota.travi.exception.ValidationException;
import com.ota.travi.repository.CustomerVoucherRepository;
import com.ota.travi.repository.KhachHangRepository;
import com.ota.travi.repository.LichSuDiemRepository;
import com.ota.travi.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CustomerLoyaltyService {

    private final KhachHangRepository khachHangRepository;
    private final CustomerVoucherRepository customerVoucherRepository;
    private final VoucherRepository voucherRepository;
    private final LichSuDiemRepository lichSuDiemRepository;
    private final LoyaltyRuleService loyaltyRuleService;
    private final PromotionService promotionService;

    // ========== LOYALTY SUMMARY ==========

    @Transactional(readOnly = true)
    public LoyaltySummaryResponse getLoyaltySummary(String customerId) {
        log.info("Fetching loyalty summary for customer: {}", customerId);
        
        KhachHang customer = khachHangRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Khách hàng không tìm thấy"));
        
        var activeRule = loyaltyRuleService.getActiveRule()
                .orElseThrow(() -> new ResourceNotFoundException("Quy tắc tích lũy điểm không được cấu hình"));
        
        int currentPoints = customer.getDiemThanhVien() != null ? customer.getDiemThanhVien() : 0;
        Double totalSpending = customer.getTongChiTieu() != null ? customer.getTongChiTieu() : 0.0;
        HangThanhVien currentTier = customer.getHangThanhVien() != null ? customer.getHangThanhVien() : HangThanhVien.DONG;
        HangThanhVien nextTier = resolveNextTier(currentTier);
        
        BigDecimal nextThreshold = thresholdFor(nextTier, activeRule);
        BigDecimal remaining = nextThreshold.subtract(BigDecimal.valueOf(totalSpending));
        double remainingAmount = Math.max(0, remaining.doubleValue());
        
        BigDecimal progressPercent = resolveProgressPercent(totalSpending, currentTier, activeRule);
        
        List<PointHistoryResponse> pointHistory = getPointHistory(customerId, 10);
        List<CustomerVoucherResponse> customerVouchers = getCustomerVouchers(customerId);
        
        return new LoyaltySummaryResponse(
            Long.valueOf(customerId),
            currentPoints,
            totalSpending,
            currentTier.name(),
            nextTier.name(),
            nextThreshold.doubleValue(),
            remainingAmount,
            progressPercent.doubleValue(),
            pointHistory,
            customerVouchers
        );
    }

    // ========== EXCHANGEABLE VOUCHERS ==========

    @Transactional(readOnly = true)
    public List<PromotionResponse> getExchangeableVouchers() {
        log.info("Fetching exchangeable vouchers");
        
        List<String> activeStatuses = Arrays.asList(
            TrangThaiUuDai.DANG_CO_HIEU_LUC.name(),
            TrangThaiUuDai.DA_LEN_LICH.name()
        );
        
        List<Voucher> vouchers = voucherRepository.findExchangeableVouchers(activeStatuses.get(0));
        
        return vouchers.stream()
                .map(this::toPromotionResponse)
                .collect(Collectors.toList());
    }

    // ========== VOUCHER EXCHANGE ==========

    @Transactional
    public ExchangeVoucherResponse exchangeVoucher(String customerId, Long voucherId) {
        log.info("Exchanging voucher {} for customer: {}", voucherId, customerId);
        
        KhachHang customer = khachHangRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Khách hàng không tìm thấy"));
        
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher không tìm thấy"));
        
        var activeRule = loyaltyRuleService.requireActiveRule();
        
        if (!voucher.getChoPhepDoiBangDiem()) {
            throw new ValidationException("Voucher này không hỗ trợ đổi bằng điểm");
        }
        
        int requiredPoints = voucher.getDiemCanDoi() != null ? voucher.getDiemCanDoi() : 0;
        int currentPoints = customer.getDiemThanhVien() != null ? customer.getDiemThanhVien() : 0;
        
        if (currentPoints < requiredPoints) {
            throw new ValidationException(
                String.format("Không đủ điểm. Yêu cầu: %d, Hiện tại: %d", requiredPoints, currentPoints)
            );
        }
        
        if (voucher.getSoLuongDaDung() >= voucher.getSoLuongPhatHanh()) {
            throw new ValidationException("Voucher đã hết");
        }
        
        int pointsBeforeExchange = currentPoints;
        int pointsAfterExchange = currentPoints - requiredPoints;
        
        try {
            customer.setDiemThanhVien(pointsAfterExchange);
            customer.setHangThanhVien(resolveTier(customer.getTongChiTieu() != null ? customer.getTongChiTieu() : 0.0, activeRule));
            khachHangRepository.save(customer);
        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn("Optimistic lock conflict during point exchange for customer: {}", customerId);
            throw new ValidationException("Giao dịch bị xung đột. Vui lòng thử lại");
        }
        
        voucher.setSoLuongDaDung(defaultUsedQuantity(voucher) + 1);
        voucherRepository.save(voucher);
        
        CustomerVoucher customerVoucher = new CustomerVoucher();
        customerVoucher.setCustomerId(customerId);
        customerVoucher.setVoucherId(voucherId);
        customerVoucher.setMaVoucherCaNhan(generateUniqueVoucherCode());
        customerVoucher.setTrangThai(TrangThaiCustomerVoucher.CHUA_DUNG);
        customerVoucher.setSourceType(SourceTypeVoucher.POINT_REDEEM);
        customerVoucher.setIssuedAt(LocalDateTime.now());
        customerVoucher.setExpiredAt(voucher.getNgayKetThuc().atTime(23, 59, 59));
        
        CustomerVoucher saved = customerVoucherRepository.save(customerVoucher);
        
        LichSuDiem history = new LichSuDiem();
        history.setCustomerId(customerId);
        history.setSoDiemThayDoi(-requiredPoints);
        history.setLoaiGiaoDichDiem(LoaiGiaoDichDiem.DOI_VOUCHER);
        history.setDiemTruocGiaoDich(pointsBeforeExchange);
        history.setDiemSauGiaoDich(pointsAfterExchange);
        history.setVoucherId(voucherId);
        history.setGhiChu("Đổi voucher: " + voucher.getMaVoucher());
        
        lichSuDiemRepository.save(history);
        
        log.info("Voucher exchanged successfully for customer {}: {}", customerId, voucherId);
        
        return new ExchangeVoucherResponse(
            saved.getId(),
            voucherId,
            voucher.getMaVoucher(),
            saved.getMaVoucherCaNhan(),
            requiredPoints,
            pointsAfterExchange,
            "Đổi voucher thành công"
        );
    }

    // ========== HELPER METHODS ==========

    @Transactional(readOnly = true)
    private List<PointHistoryResponse> getPointHistory(String customerId, int limit) {
        List<LichSuDiem> histories = lichSuDiemRepository.findByKhachHang_IdOrderByCreatedAtDesc(Long.valueOf(customerId));
        
        return histories.stream()
                .limit(limit)
                .map(this::toPointHistoryResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    private List<CustomerVoucherResponse> getCustomerVouchers(String customerId) {
        List<String> availableStatuses = Arrays.asList(
            TrangThaiCustomerVoucher.CHUA_DUNG.name(),
            TrangThaiCustomerVoucher.DA_DUNG.name()
        );
        
        List<CustomerVoucher> customerVouchers = customerVoucherRepository.findByKhachHang_IdAndTrangThaiIn(
            Long.valueOf(customerId),
            availableStatuses
        );
        
        return customerVouchers.stream()
                .map(this::toCustomerVoucherResponse)
                .collect(Collectors.toList());
    }

    private PromotionResponse toPromotionResponse(Voucher voucher) {
        return new PromotionResponse(
            voucher.getId(),
            voucher.getTenUuDai(),
            normalizeNullable(voucher.getMoTa()),
            voucher.getCreatedByUserId(),
            voucher.getCreatedByRole() != null ? voucher.getCreatedByRole().name() : null,
            voucher.getBusinessProfileId(),
            voucher.getMucGiam(),
            voucher.getLoaiGiamGia().name(),
            voucher.getGiaTriGiamToiDa(),
            voucher.getNgayBatDau().atStartOfDay(),
            voucher.getNgayKetThuc().atTime(23, 59, 59),
            voucher.getTrangThaiUuDai().name(),
            voucher.getDeleted(),
            "VOUCHER",
            voucher.getMaVoucher(),
            voucher.getSoLuongPhatHanh(),
            voucher.getSoLuongDaDung(),
            voucher.getDonHangToiThieu(),
            voucher.getUsageLimitPerUser(),
            voucher.getDiemCanDoi(),
            voucher.getChoPhepDoiBangDiem(),
            voucher.getPhamViApDung() != null ? voucher.getPhamViApDung().name() : null,
            null,
            null,
            voucher.getCreatedAt(),
            voucher.getUpdatedAt()
        );
    }

    private PointHistoryResponse toPointHistoryResponse(LichSuDiem lichSuDiem) {
        return new PointHistoryResponse(
            lichSuDiem.getId(),
            Long.valueOf(lichSuDiem.getCustomerId()),
            lichSuDiem.getSoDiemThayDoi(),
            lichSuDiem.getLoaiGiaoDichDiem().name(),
            lichSuDiem.getDiemTruocGiaoDich(),
            lichSuDiem.getDiemSauGiaoDich(),
            lichSuDiem.getBookingId(),
            lichSuDiem.getVoucherId(),
            normalizeNullable(lichSuDiem.getGhiChu()),
            lichSuDiem.getCreatedAt()
        );
    }

    private CustomerVoucherResponse toCustomerVoucherResponse(CustomerVoucher cv) {
        return new CustomerVoucherResponse(
            cv.getId(),
            cv.getVoucherId(),
            cv.getCustomerId(),
            null,
            cv.getMaVoucherCaNhan(),
            null,
            cv.getTrangThai().name(),
            cv.getSourceType() != null ? cv.getSourceType().name() : null,
            cv.getIssuedAt(),
            cv.getReservedAt(),
            cv.getReserveExpiresAt(),
            cv.getUsedAt(),
            cv.getExpiredAt(),
            cv.getBookingId(),
            CustomerVoucherResponse.calculateIsReserveExpired(cv.getReserveExpiresAt())
        );
    }

    private String normalizeNullable(String value) {
        return value != null ? value : "";
    }

    private HangThanhVien resolveTier(Double totalSpending, LoyaltyRule rule) {
        if (totalSpending >= rule.getDiamondThreshold()) {
            return HangThanhVien.KIM_CUONG;
        } else if (totalSpending >= rule.getGoldThreshold()) {
            return HangThanhVien.VANG;
        } else if (totalSpending >= rule.getSilverThreshold()) {
            return HangThanhVien.BAC;
        } else {
            return HangThanhVien.DONG;
        }
    }

    private HangThanhVien resolveNextTier(HangThanhVien currentTier) {
        return switch (currentTier) {
            case DONG -> HangThanhVien.BAC;
            case BAC -> HangThanhVien.VANG;
            case VANG -> HangThanhVien.KIM_CUONG;
            case KIM_CUONG -> HangThanhVien.KIM_CUONG;
        };
    }

    private BigDecimal thresholdFor(HangThanhVien tier, LoyaltyRule rule) {
        return switch (tier) {
            case DONG -> BigDecimal.ZERO;
            case BAC -> BigDecimal.valueOf(rule.getSilverThreshold());
            case VANG -> BigDecimal.valueOf(rule.getGoldThreshold());
            case KIM_CUONG -> BigDecimal.valueOf(rule.getDiamondThreshold());
        };
    }

    private BigDecimal resolveProgressPercent(
            Double spending,
            HangThanhVien currentTier,
            LoyaltyRule rule) {
        
        BigDecimal currentSpending = BigDecimal.valueOf(spending != null ? spending : 0.0);
        BigDecimal currentThreshold = thresholdFor(currentTier, rule);
        HangThanhVien nextTier = resolveNextTier(currentTier);
        BigDecimal nextThreshold = thresholdFor(nextTier, rule);
        
        if (nextThreshold.equals(currentThreshold)) {
            return BigDecimal.valueOf(100);
        }
        
        BigDecimal progress = currentSpending.subtract(currentThreshold);
        BigDecimal range = nextThreshold.subtract(currentThreshold);
        
        return progress.divide(range, 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100));
    }

    private int defaultUsedQuantity(Voucher voucher) {
        return voucher.getSoLuongDaDung() != null ? voucher.getSoLuongDaDung() : 0;
    }

    private String generateUniqueVoucherCode() {
        return UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }
}
