package com.ota.travi.service;

import com.ota.travi.dto.response.CustomerVoucherResponse;
import com.ota.travi.dto.response.ExchangeVoucherResponse;
import com.ota.travi.dto.response.LoyaltyProgressResponse;
import com.ota.travi.dto.response.LoyaltySummaryResponse;
import com.ota.travi.dto.response.PointHistoryResponse;
import com.ota.travi.dto.response.PromotionResponse;
import com.ota.travi.entity.CustomerVoucher;
import com.ota.travi.entity.DonDatCho;
import com.ota.travi.entity.KhachHang;
import com.ota.travi.entity.LichSuDiem;
import com.ota.travi.entity.LoyaltyRule;
import com.ota.travi.entity.Voucher;
import com.ota.travi.enums.HangThanhVien;
import com.ota.travi.enums.LoaiGiaoDichDiem;
import com.ota.travi.enums.SourceTypeVoucher;
import com.ota.travi.enums.TrangThaiCustomerVoucher;
import com.ota.travi.enums.TrangThaiDon;
import com.ota.travi.enums.TrangThaiUuDai;
import com.ota.travi.exception.ResourceNotFoundException;
import com.ota.travi.exception.ValidationException;
import com.ota.travi.repository.CustomerVoucherRepository;
import com.ota.travi.repository.KhachHangRepository;
import com.ota.travi.repository.LichSuDiemRepository;
import com.ota.travi.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    private final MilestoneRewardService milestoneRewardService;
    private final NotificationEventService notificationEventService;

    @Transactional(readOnly = true)
    public LoyaltySummaryResponse getLoyaltySummary(String customerId) {
        KhachHang customer = khachHangRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Khach hang khong tim thay"));

        int currentPoints = customer.getDiemThanhVien() != null ? customer.getDiemThanhVien() : 0;
        LoyaltyProgressResponse progress = getLoyaltyProgress(customerId);
        List<PointHistoryResponse> pointHistory = getPointHistory(customerId, 10);
        List<CustomerVoucherResponse> customerVouchers = getWalletVouchers(customerId);

        return new LoyaltySummaryResponse(
                customerId,
                currentPoints,
                progress.totalSpending(),
                progress.currentTier(),
                progress.nextTier(),
                progress.requiredSpending(),
                progress.remainingSpending(),
                progress.progressPercent(),
                pointHistory,
                customerVouchers
        );
    }

    @Transactional(readOnly = true)
    public LoyaltyProgressResponse getLoyaltyProgress(String customerId) {
        KhachHang customer = khachHangRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Khach hang khong tim thay"));
        LoyaltyRule activeRule = loyaltyRuleService.requireActiveRule();

        double totalSpending = normalizeMoney(customer.getTongChiTieu());
        HangThanhVien currentTier = customer.getHangThanhVien() != null ? customer.getHangThanhVien() : HangThanhVien.DONG;
        HangThanhVien nextTier = resolveNextTier(currentTier);
        double nextThreshold = thresholdFor(nextTier, activeRule).doubleValue();
        double remainingAmount = Math.max(0, nextThreshold - totalSpending);
        double progressPercent = resolveProgressPercent(totalSpending, currentTier, activeRule).doubleValue();

        return new LoyaltyProgressResponse(
                currentTier.name(),
                totalSpending,
                nextTier.name(),
                nextThreshold,
                remainingAmount,
                progressPercent
        );
    }

    @Transactional(readOnly = true)
    public List<PointHistoryResponse> getPointHistory(String customerId, Integer limit) {
        int safeLimit = (limit == null || limit <= 0) ? 20 : Math.min(limit, 100);
        return loadPointHistory(customerId, safeLimit);
    }

    @Transactional(readOnly = true)
    public List<CustomerVoucherResponse> getWalletVouchers(String customerId) {
        List<CustomerVoucher> customerVouchers = customerVoucherRepository.findByCustomerIdOrderByIssuedAtDesc(customerId);
        return mapCustomerVoucherResponses(customerVouchers);
    }

    @Transactional
    public void processBookingCompletionReward(DonDatCho booking) {
        if (booking == null || booking.getTrangThai() != TrangThaiDon.DA_HOAN_THANH) {
            return;
        }

        String customerId = booking.getKhachHang().getId();
        String rewardNote = "Tich diem cho don " + booking.getId();

        if (lichSuDiemRepository.existsByCustomerIdAndLoaiGiaoDichDiemAndGhiChu(
                customerId,
                LoaiGiaoDichDiem.TICH_DIEM,
                rewardNote)) {
            return;
        }

        LoyaltyRule activeRule = loyaltyRuleService.requireActiveRule();
        KhachHang customer = khachHangRepository.findByIdForUpdate(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Khach hang khong tim thay"));

        int pointsBefore = customer.getDiemThanhVien() != null ? customer.getDiemThanhVien() : 0;
        double totalSpendingBefore = normalizeMoney(customer.getTongChiTieu());
        HangThanhVien tierBefore = customer.getHangThanhVien() != null ? customer.getHangThanhVien() : HangThanhVien.DONG;
        double paidAmount = normalizeMoney(booking.getTongTienThanhToan());

        int earnedPoints = calculateEarnedPoints(paidAmount, tierBefore, activeRule);
        int pointsAfter = pointsBefore + earnedPoints;
        double totalSpendingAfter = totalSpendingBefore + paidAmount;
        HangThanhVien tierAfter = resolveTier(totalSpendingAfter, activeRule);

        customer.setDiemThanhVien(pointsAfter);
        customer.setTongChiTieu(totalSpendingAfter);
        customer.setHangThanhVien(tierAfter);
        khachHangRepository.save(customer);

        LichSuDiem history = new LichSuDiem();
        history.setCustomerId(customerId);
        history.setSoDiemThayDoi(earnedPoints);
        history.setLoaiGiaoDichDiem(LoaiGiaoDichDiem.TICH_DIEM);
        history.setDiemTruocGiaoDich(pointsBefore);
        history.setDiemSauGiaoDich(pointsAfter);
        history.setBookingId(booking.getId());
        history.setGhiChu(rewardNote);
        lichSuDiemRepository.save(history);

        milestoneRewardService.incrementBookingCount(customerId);
        notificationEventService.emitPointEarned(customerId, earnedPoints, booking.getId());
        if (tierAfter != tierBefore) {
            notificationEventService.emitTierUpgraded(customerId, tierAfter.name(), totalSpendingAfter);
        }
    }

    @Transactional(readOnly = true)
    public List<PromotionResponse> getExchangeableVouchers() {
        LocalDate today = LocalDate.now();
        List<Voucher> vouchers = voucherRepository.findExchangeableVouchers().stream()
                .filter(voucher -> voucher.getTrangThaiUuDai() == TrangThaiUuDai.DANG_CO_HIEU_LUC)
                .filter(voucher -> voucher.getNgayBatDau() == null || !voucher.getNgayBatDau().isAfter(today))
                .filter(voucher -> voucher.getNgayKetThuc() == null || !voucher.getNgayKetThuc().isBefore(today))
                .filter(voucher -> customerVoucherRepository.countByVoucherId(voucher.getId()) < safeIssuedQuantityLimit(voucher))
                .toList();
        return vouchers.stream().map(this::toPromotionResponse).collect(Collectors.toList());
    }

    @Transactional
    public ExchangeVoucherResponse exchangeVoucher(String customerId, Long voucherId) {
        KhachHang customer = khachHangRepository.findByIdForUpdate(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Khach hang khong tim thay"));

        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher khong tim thay"));

        if (voucher.getDeleted() || voucher.getTrangThaiUuDai() != TrangThaiUuDai.DANG_CO_HIEU_LUC) {
            throw new ValidationException("Voucher khong kha dung");
        }
        if (voucher.getNgayBatDau() != null && voucher.getNgayBatDau().isAfter(LocalDate.now())) {
            throw new ValidationException("Voucher chua den thoi gian ap dung");
        }
        if (voucher.getNgayKetThuc() != null && voucher.getNgayKetThuc().isBefore(LocalDate.now())) {
            throw new ValidationException("Voucher da het han");
        }
        if (!Boolean.TRUE.equals(voucher.getChoPhepDoiBangDiem())) {
            throw new ValidationException("Voucher nay khong ho tro doi bang diem");
        }
        if (customerVoucherRepository.countByVoucherId(voucherId) >= safeIssuedQuantityLimit(voucher)) {
            throw new ValidationException("Voucher da het");
        }

        int requiredPoints = voucher.getDiemCanDoi() != null ? voucher.getDiemCanDoi() : 0;
        int currentPoints = customer.getDiemThanhVien() != null ? customer.getDiemThanhVien() : 0;
        if (currentPoints < requiredPoints) {
            throw new ValidationException("Khong du diem de doi voucher");
        }

        int pointsAfterExchange = currentPoints - requiredPoints;
        customer.setDiemThanhVien(pointsAfterExchange);
        customer.setHangThanhVien(resolveTier(normalizeMoney(customer.getTongChiTieu()), loyaltyRuleService.requireActiveRule()));
        khachHangRepository.save(customer);

        CustomerVoucher customerVoucher = new CustomerVoucher();
        customerVoucher.setCustomerId(customerId);
        customerVoucher.setVoucherId(voucherId);
        customerVoucher.setMaVoucherCaNhan(generateUniqueVoucherCode());
        customerVoucher.setTrangThai(TrangThaiCustomerVoucher.CHUA_DUNG);
        customerVoucher.setSourceType(SourceTypeVoucher.POINT_REDEEM);
        customerVoucher.setIssuedAt(LocalDateTime.now());
        customerVoucher.setExpiredAt(voucher.getNgayKetThuc() != null ? voucher.getNgayKetThuc().atTime(23, 59, 59) : null);
        CustomerVoucher saved = customerVoucherRepository.save(customerVoucher);

        LichSuDiem history = new LichSuDiem();
        history.setCustomerId(customerId);
        history.setSoDiemThayDoi(-requiredPoints);
        history.setLoaiGiaoDichDiem(LoaiGiaoDichDiem.DOI_VOUCHER);
        history.setDiemTruocGiaoDich(currentPoints);
        history.setDiemSauGiaoDich(pointsAfterExchange);
        history.setVoucherId(voucherId);
        history.setGhiChu("Doi voucher: " + voucher.getMaVoucher());
        lichSuDiemRepository.save(history);

        notificationEventService.emitVoucherReceived(customerId, voucherId, SourceTypeVoucher.POINT_REDEEM.name());

        return new ExchangeVoucherResponse(
                saved.getId(),
                voucherId,
                voucher.getMaVoucher(),
                saved.getMaVoucherCaNhan(),
                requiredPoints,
                pointsAfterExchange,
                "Doi voucher thanh cong"
        );
    }

    @Transactional(readOnly = true)
    private List<PointHistoryResponse> loadPointHistory(String customerId, int limit) {
        return lichSuDiemRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .limit(limit)
                .map(this::toPointHistoryResponse)
                .collect(Collectors.toList());
    }

    private List<CustomerVoucherResponse> mapCustomerVoucherResponses(List<CustomerVoucher> customerVouchers) {
        List<Long> voucherIds = customerVouchers.stream()
                .map(CustomerVoucher::getVoucherId)
                .distinct()
                .toList();
        Map<Long, Voucher> voucherMap = voucherRepository.findAllById(voucherIds).stream()
                .collect(Collectors.toMap(Voucher::getId, v -> v, (left, right) -> left));

        return customerVouchers.stream()
                .map(cv -> toCustomerVoucherResponse(cv, voucherMap.get(cv.getVoucherId())))
                .collect(Collectors.toList());
    }

    private PromotionResponse toPromotionResponse(Voucher voucher) {
        return new PromotionResponse(
                voucher.getId(),
                voucher.getTenUuDai(),
                voucher.getMoTa(),
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
                lichSuDiem.getCustomerId(),
                lichSuDiem.getSoDiemThayDoi(),
                lichSuDiem.getLoaiGiaoDichDiem().name(),
                lichSuDiem.getDiemTruocGiaoDich(),
                lichSuDiem.getDiemSauGiaoDich(),
                lichSuDiem.getBookingId(),
                lichSuDiem.getVoucherId(),
                lichSuDiem.getGhiChu(),
                lichSuDiem.getCreatedAt()
        );
    }

    private CustomerVoucherResponse toCustomerVoucherResponse(CustomerVoucher cv, Voucher voucher) {
        return new CustomerVoucherResponse(
                cv.getId(),
                cv.getVoucherId(),
                cv.getCustomerId(),
                voucher != null ? voucher.getMaVoucher() : null,
                cv.getMaVoucherCaNhan(),
                voucher != null ? voucher.getTenUuDai() : null,
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

    private HangThanhVien resolveTier(Double totalSpending, LoyaltyRule rule) {
        if (totalSpending >= rule.getDiamondThreshold()) {
            return HangThanhVien.KIM_CUONG;
        }
        if (totalSpending >= rule.getGoldThreshold()) {
            return HangThanhVien.VANG;
        }
        if (totalSpending >= rule.getSilverThreshold()) {
            return HangThanhVien.BAC;
        }
        return HangThanhVien.DONG;
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

    private BigDecimal resolveProgressPercent(Double spending, HangThanhVien currentTier, LoyaltyRule rule) {
        BigDecimal currentSpending = BigDecimal.valueOf(spending != null ? spending : 0.0);
        BigDecimal currentThreshold = thresholdFor(currentTier, rule);
        HangThanhVien nextTier = resolveNextTier(currentTier);
        BigDecimal nextThreshold = thresholdFor(nextTier, rule);

        if (nextThreshold.equals(currentThreshold)) {
            return BigDecimal.valueOf(100);
        }

        BigDecimal progress = currentSpending.subtract(currentThreshold);
        BigDecimal range = nextThreshold.subtract(currentThreshold);
        return progress.divide(range, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .max(BigDecimal.ZERO)
                .min(BigDecimal.valueOf(100));
    }

    private int safeIssuedQuantityLimit(Voucher voucher) {
        return voucher.getSoLuongPhatHanh() != null ? voucher.getSoLuongPhatHanh() : 0;
    }

    private String generateUniqueVoucherCode() {
        return UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }

    private int calculateEarnedPoints(double paidAmount, HangThanhVien tier, LoyaltyRule rule) {
        if (paidAmount <= 0 || rule.getMoneyPerPoint() == null || rule.getMoneyPerPoint() <= 0) {
            return 0;
        }
        double basePoints = paidAmount / rule.getMoneyPerPoint();
        double multiplier = switch (tier) {
            case BAC -> rule.getSilverMultiplier();
            case VANG -> rule.getGoldMultiplier();
            case KIM_CUONG -> rule.getDiamondMultiplier();
            default -> 1.0;
        };
        return (int) Math.floor(basePoints * multiplier);
    }

    private double normalizeMoney(Double value) {
        return value == null ? 0.0 : value;
    }
}
