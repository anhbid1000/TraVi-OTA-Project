package com.ota.travi.service;

import com.ota.travi.dto.request.PromotionCreateRequest;
import com.ota.travi.dto.request.VoucherCreateRequest;
import com.ota.travi.dto.response.PromotionResponse;
import com.ota.travi.dto.response.VoucherApplyResponse;
import com.ota.travi.entity.KhachHang;
import com.ota.travi.entity.LichSuDiem;
import com.ota.travi.entity.UuDai;
import com.ota.travi.entity.Voucher;
import com.ota.travi.entity.HoSoKinhDoanh;
import com.ota.travi.enums.CreatedByRole;
import com.ota.travi.enums.HangThanhVien;
import com.ota.travi.enums.LoaiGiamGia;
import com.ota.travi.enums.TrangThaiUuDai;
import com.ota.travi.enums.LoaiGiaoDichDiem;
import com.ota.travi.exception.BusinessConflictException;
import com.ota.travi.exception.ForbiddenOperationException;
import com.ota.travi.exception.ResourceNotFoundException;
import com.ota.travi.exception.ValidationException;
import com.ota.travi.repository.HoSoKinhDoanhRepository;
import com.ota.travi.repository.KhachHangRepository;
import com.ota.travi.repository.LichSuDiemRepository;
import com.ota.travi.repository.UuDaiRepository;
import com.ota.travi.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PromotionService {

    private final UuDaiRepository uuDaiRepository;
    private final VoucherRepository voucherRepository;
    private final KhachHangRepository khachHangRepository;
    private final LichSuDiemRepository lichSuDiemRepository;
    private final HoSoKinhDoanhRepository hoSoKinhDoanhRepository;
    private final LoyaltyRuleService loyaltyRuleService;

    // ========== PROMOTION CREATION ==========

    @Transactional
    public PromotionResponse createDirectPromotion(
            PromotionCreateRequest request,
            String createdByUserId,
            Boolean isAdmin) {
        
        log.info("Creating direct promotion for user: {}", createdByUserId);
        
        validateDateRange(request.ngayBatDau(), request.ngayKetThuc());
        validatePartnerPromotionScope(request, createdByUserId, isAdmin);
        
        checkOverlapPromotions(
            request.targetType(),
            request.targetId(),
            request.ngayBatDau(),
            request.ngayKetThuc()
        );
        
        UuDai promotion = new UuDai();
        promotion.setTenUuDai(request.tenUuDai());
        promotion.setMoTa(request.moTa());
        promotion.setCreatedByUserId(createdByUserId);
        promotion.setCreatedByRole(isAdmin ? CreatedByRole.QUAN_TRI_VIEN : CreatedByRole.DOI_TAC);
        promotion.setBusinessProfileId(request.businessProfileId());
        promotion.setMucGiam(request.mucGiam());
        promotion.setLoaiGiamGia(LoaiGiamGia.valueOf(request.loaiGiamGia()));
        promotion.setGiaTriGiamToiDa(request.giaTriGiamToiDa());
        promotion.setNgayBatDau(request.ngayBatDau().toLocalDate());
        promotion.setNgayKetThuc(request.ngayKetThuc().toLocalDate());
        promotion.setDeleted(false);
        promotion.setTrangThaiUuDai(resolveActiveStatus(request.ngayBatDau(), request.ngayKetThuc()));
        
        UuDai saved = uuDaiRepository.save(promotion);
        log.info("Direct promotion created with ID: {}", saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public PromotionResponse createVoucher(
            VoucherCreateRequest request,
            String createdByUserId,
            Boolean isAdmin) {
        
        log.info("Creating voucher with code: {}", request.maVoucher());
        
        validateDateRange(request.ngayBatDau(), request.ngayKetThuc());
        validatePartnerVoucherScope(request, createdByUserId, isAdmin);
        
        if (voucherRepository.existsByMaVoucherIgnoreCase(request.maVoucher())) {
            throw new BusinessConflictException("Mã voucher '" + request.maVoucher() + "' đã tồn tại");
        }
        
        Voucher voucher = new Voucher();
        voucher.setTenUuDai(request.tenUuDai());
        voucher.setMoTa(request.moTa());
        voucher.setCreatedByUserId(createdByUserId);
        voucher.setCreatedByRole(isAdmin ? CreatedByRole.QUAN_TRI_VIEN : CreatedByRole.DOI_TAC);
        voucher.setBusinessProfileId(request.businessProfileId());
        voucher.setMucGiam(request.mucGiam());
        voucher.setLoaiGiamGia(LoaiGiamGia.valueOf(request.loaiGiamGia()));
        voucher.setGiaTriGiamToiDa(request.giaTriGiamToiDa());
        voucher.setNgayBatDau(request.ngayBatDau().toLocalDate());
        voucher.setNgayKetThuc(request.ngayKetThuc().toLocalDate());
        voucher.setMaVoucher(request.maVoucher());
        voucher.setSoLuongPhatHanh(request.soLuongPhatHanh());
        voucher.setSoLuongDaDung(0);
        voucher.setDonHangToiThieu(request.donHangToiThieu() != null ? request.donHangToiThieu() : 0.0);
        voucher.setUsageLimitPerUser(request.usageLimitPerUser());
        voucher.setDiemCanDoi(request.diemCanDoi());
        voucher.setChoPhepDoiBangDiem(request.choPhepDoiBangDiem() != null ? request.choPhepDoiBangDiem() : false);
        if (request.phamViApDung() != null) {
            voucher.setPhamViApDung(com.ota.travi.enums.PhamViApDung.valueOf(request.phamViApDung()));
        }
        voucher.setDeleted(false);
        voucher.setTrangThaiUuDai(resolveActiveStatus(request.ngayBatDau(), request.ngayKetThuc()));
        
        Voucher saved = voucherRepository.save(voucher);
        log.info("Voucher created with ID: {} and code: {}", saved.getId(), saved.getMaVoucher());
        return toResponse(saved);
    }

    // ========== PROMOTION RETRIEVAL ==========

    @Transactional(readOnly = true)
    public List<PromotionResponse> getPromotionsByPartner(String partnerId) {
        log.info("Fetching promotions for partner: {}", partnerId);
        
        List<UuDai> promotions = uuDaiRepository.findByCreatedByUserIdAndDeletedFalse(partnerId);
        return promotions.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PromotionResponse> getAllPromotions() {
        log.info("Fetching all active promotions");
        
        List<String> activeStatuses = Arrays.asList(
            TrangThaiUuDai.DA_LEN_LICH.name(),
            TrangThaiUuDai.DANG_CO_HIEU_LUC.name()
        );
        
        List<UuDai> promotions = uuDaiRepository.findByDeletedFalseAndTrangThaiUuDaiIn(activeStatuses);
        return promotions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PromotionResponse getPromotionById(Long id) {
        log.info("Fetching promotion with ID: {}", id);
        
        UuDai promotion = uuDaiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ưu đãi không tìm thấy"));
        
        if (promotion.getDeleted()) {
            throw new ResourceNotFoundException("Ưu đãi không tìm thấy");
        }
        
        return toResponse(promotion);
    }

    // ========== PROMOTION STATUS MANAGEMENT ==========

    @Transactional
    public PromotionResponse pausePromotion(Long id, String actorId, Boolean isAdmin) {
        log.info("Pausing promotion: {}", id);
        
        UuDai promotion = uuDaiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ưu đãi không tìm thấy"));
        
        requireAccessiblePromotion(promotion, actorId, isAdmin);
        
        if (!TrangThaiUuDai.DANG_CO_HIEU_LUC.equals(promotion.getTrangThaiUuDai())) {
            throw new ValidationException("Chỉ có thể tạm dừng ưu đãi đang hoạt động");
        }
        
        promotion.setTrangThaiUuDai(TrangThaiUuDai.TAM_DUNG);
        UuDai saved = uuDaiRepository.save(promotion);
        log.info("Promotion paused: {}", id);
        return toResponse(saved);
    }

    @Transactional
    public PromotionResponse resumePromotion(Long id, String actorId, Boolean isAdmin) {
        log.info("Resuming promotion: {}", id);
        
        UuDai promotion = uuDaiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ưu đãi không tìm thấy"));
        
        requireAccessiblePromotion(promotion, actorId, isAdmin);
        
        if (!TrangThaiUuDai.TAM_DUNG.equals(promotion.getTrangThaiUuDai())) {
            throw new ValidationException("Chỉ có thể tiếp tục ưu đãi đã tạm dừng");
        }
        
        promotion.setTrangThaiUuDai(
            resolveActiveStatus(
                promotion.getNgayBatDau().atStartOfDay(),
                promotion.getNgayKetThuc().atTime(23, 59, 59)
            )
        );
        UuDai saved = uuDaiRepository.save(promotion);
        log.info("Promotion resumed: {}", id);
        return toResponse(saved);
    }

    // ========== SCHEDULED TASKS ==========

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void autoUpdateExpiredPromotions() {
        log.info("Running scheduled task to update expired promotions");
        
        LocalDateTime now = LocalDateTime.now();
        List<String> activeStatuses = Arrays.asList(
            TrangThaiUuDai.DA_LEN_LICH.name(),
            TrangThaiUuDai.DANG_CO_HIEU_LUC.name()
        );
        
        uuDaiRepository.updateStatusForExpiredPromotions(
            TrangThaiUuDai.DA_HET_HAN.name(),
            now,
            activeStatuses
        );
        
        log.info("Expired promotions updated");
    }

    // ========== VOUCHER APPLICATION ==========

    @Transactional(readOnly = true)
    public VoucherApplyResponse applyVoucher(String voucherCode, Double originalTotal) {
        log.info("Applying voucher: {}", voucherCode);
        
        Voucher voucher = voucherRepository.findByMaVoucher(voucherCode)
                .orElseThrow(() -> new ResourceNotFoundException("Mã voucher không hợp lệ"));
        
        if (voucher.getDeleted() || !TrangThaiUuDai.DANG_CO_HIEU_LUC.equals(voucher.getTrangThaiUuDai())) {
            throw new ValidationException("Voucher không có sẵn");
        }
        
        if (voucher.getSoLuongDaDung() >= voucher.getSoLuongPhatHanh()) {
            throw new ValidationException("Voucher đã hết");
        }
        
        if (originalTotal < (voucher.getDonHangToiThieu() != null ? voucher.getDonHangToiThieu() : 0)) {
            throw new ValidationException("Tổng đơn hàng không đạt yêu cầu tối thiểu");
        }
        
        double discountAmount = calculateDiscountAmount(originalTotal, voucher);
        double newTotal = Math.max(0, originalTotal - discountAmount);
        
        log.info("Voucher applied successfully: {} (discount: {})", voucherCode, discountAmount);
        
        return new VoucherApplyResponse(
            voucherCode,
            discountAmount,
            originalTotal,
            newTotal,
            "Áp dụng mã voucher thành công"
        );
    }

    // ========== POINT ACCUMULATION ==========

    @Transactional
    public void accumulatePoints(String customerId, Double bookingAmount, Long bookingId) {
        log.info("Accumulating points for customer: {} with amount: {}", customerId, bookingAmount);
        
        KhachHang customer = khachHangRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Khách hàng không tìm thấy"));
        
        var activeRule = loyaltyRuleService.requireActiveRule();
        
        int earnedPoints = calculatePointsEarned(bookingAmount, customer, activeRule);
        int previousPoints = customer.getDiemThanhVien() != null ? customer.getDiemThanhVien() : 0;
        int newPoints = previousPoints + earnedPoints;
        
        customer.setDiemThanhVien(newPoints);
        customer.setTongChiTieu((customer.getTongChiTieu() != null ? customer.getTongChiTieu() : 0.0) + bookingAmount);
        customer.setHangThanhVien(resolveTier(customer.getTongChiTieu(), activeRule));
        
        khachHangRepository.save(customer);
        
        LichSuDiem history = new LichSuDiem();
        history.setCustomerId(customerId);
        history.setSoDiemThayDoi(earnedPoints);
        history.setLoaiGiaoDichDiem(LoaiGiaoDichDiem.TICH_DIEM);
        history.setDiemTruocGiaoDich(previousPoints);
        history.setDiemSauGiaoDich(newPoints);
        history.setBookingId(bookingId);
        history.setGhiChu("Tích điểm từ đơn đặt có giá trị: " + bookingAmount);
        
        lichSuDiemRepository.save(history);
        
        log.info("Points accumulated for customer {}: {} points earned", customerId, earnedPoints);
    }

    // ========== VALIDATION METHODS ==========

    private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (endDate == null || startDate == null) {
            throw new ValidationException("Ngày bắt đầu và ngày kết thúc không được để trống");
        }
        
        if (!endDate.isAfter(startDate)) {
            throw new ValidationException("Ngày kết thúc phải sau ngày bắt đầu");
        }
    }

    private void validatePartnerPromotionScope(
            PromotionCreateRequest request,
            String partnerId,
            Boolean isAdmin) {
        
        HoSoKinhDoanh bioProfile = hoSoKinhDoanhRepository.findById(String.valueOf(request.businessProfileId()))
                .orElseThrow(() -> new ResourceNotFoundException("Hồ sơ kinh doanh không tìm thấy"));
        
        if (!isAdmin && !bioProfile.getDoiTac().getId().equals(partnerId)) {
            throw new ForbiddenOperationException("Bạn không có quyền tạo ưu đãi cho hồ sơ này");
        }
    }

    private void validatePartnerVoucherScope(
            VoucherCreateRequest request,
            String partnerId,
            Boolean isAdmin) {
        
        HoSoKinhDoanh bioProfile = hoSoKinhDoanhRepository.findById(String.valueOf(request.businessProfileId()))
                .orElseThrow(() -> new ResourceNotFoundException("Hồ sơ kinh doanh không tìm thấy"));
        
        if (!isAdmin && !bioProfile.getDoiTac().getId().equals(partnerId)) {
            throw new ForbiddenOperationException("Bạn không có quyền tạo voucher cho hồ sơ này");
        }
    }

    private void checkOverlapPromotions(
            String targetType,
            Long targetId,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        
        List<String> activeStatuses = Arrays.asList(
            TrangThaiUuDai.DA_LEN_LICH.name(),
            TrangThaiUuDai.DANG_CO_HIEU_LUC.name()
        );
        
        if (targetType != null && targetId != null) {
            boolean hasOverlap = uuDaiRepository.existsOverlappingPromotion(
                targetType,
                targetId,
                startDate,
                endDate,
                activeStatuses
            );
            
            if (hasOverlap) {
                throw new BusinessConflictException("Đã tồn tại ưu đãi trùng lịch");
            }
        }
    }

    private void requireAccessiblePromotion(UuDai promotion, String actorId, Boolean isAdmin) {
        if (!isAdmin && !promotion.getCreatedByUserId().equals(actorId)) {
            throw new ForbiddenOperationException("Bạn không có quyền chỉnh sửa ưu đãi này");
        }
    }

    // ========== CONVERSION METHODS ==========

    private PromotionResponse toResponse(UuDai uuDai) {
        String maVoucher = null;
        Integer soLuongPhatHanh = null;
        Integer soLuongDaDung = null;
        Double donHangToiThieu = null;
        Integer usageLimitPerUser = null;
        Integer diemCanDoi = null;
        Boolean choPhepDoiBangDiem = null;
        String phamViApDung = null;
        String loaiUuDai = "DIRECT_PROMOTION";
        
        if (uuDai instanceof Voucher) {
            Voucher voucher = (Voucher) uuDai;
            maVoucher = voucher.getMaVoucher();
            soLuongPhatHanh = voucher.getSoLuongPhatHanh();
            soLuongDaDung = voucher.getSoLuongDaDung();
            donHangToiThieu = voucher.getDonHangToiThieu();
            usageLimitPerUser = voucher.getUsageLimitPerUser();
            diemCanDoi = voucher.getDiemCanDoi();
            choPhepDoiBangDiem = voucher.getChoPhepDoiBangDiem();
            if (voucher.getPhamViApDung() != null) {
                phamViApDung = voucher.getPhamViApDung().name();
            }
            loaiUuDai = "VOUCHER";
        }
        
        return new PromotionResponse(
            uuDai.getId(),
            uuDai.getTenUuDai(),
            normalizeNullable(uuDai.getMoTa()),
            uuDai.getCreatedByUserId(),
            uuDai.getCreatedByRole() != null ? uuDai.getCreatedByRole().name() : null,
            uuDai.getBusinessProfileId(),
            uuDai.getMucGiam(),
            uuDai.getLoaiGiamGia().name(),
            uuDai.getGiaTriGiamToiDa(),
            uuDai.getNgayBatDau().atStartOfDay(),
            uuDai.getNgayKetThuc().atTime(23, 59, 59),
            uuDai.getTrangThaiUuDai().name(),
            uuDai.getDeleted(),
            loaiUuDai,
            maVoucher,
            soLuongPhatHanh,
            soLuongDaDung,
            donHangToiThieu,
            usageLimitPerUser,
            diemCanDoi,
            choPhepDoiBangDiem,
            phamViApDung,
            null,
            null,
            uuDai.getCreatedAt(),
            uuDai.getUpdatedAt()
        );
    }

    private String normalizeNullable(String value) {
        return value != null ? value : "";
    }

    // ========== HELPER METHODS ==========

    private TrangThaiUuDai resolveActiveStatus(LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();
        
        if (now.isBefore(startDate)) {
            return TrangThaiUuDai.DA_LEN_LICH;
        } else if (now.isAfter(endDate)) {
            return TrangThaiUuDai.DA_HET_HAN;
        } else {
            return TrangThaiUuDai.DANG_CO_HIEU_LUC;
        }
    }

    private double calculateDiscountAmount(Double originalTotal, Voucher voucher) {
        double discount = 0;
        
        if (LoaiGiamGia.PHAN_TRAM.equals(voucher.getLoaiGiamGia())) {
            discount = originalTotal * (voucher.getMucGiam() / 100.0);
        } else {
            discount = voucher.getMucGiam();
        }
        
        if (voucher.getGiaTriGiamToiDa() != null) {
            discount = Math.min(discount, voucher.getGiaTriGiamToiDa());
        }
        
        return discount;
    }

    private int calculatePointsEarned(
            Double bookingAmount,
            KhachHang customer,
            com.ota.travi.entity.LoyaltyRule rule) {
        
        BigDecimal amount = BigDecimal.valueOf(bookingAmount);
        BigDecimal moneyPerPoint = BigDecimal.valueOf(rule.getMoneyPerPoint());
        
        int basePoints = amount.divide(moneyPerPoint, BigDecimal.ROUND_DOWN).intValue();
        
        HangThanhVien tier = resolveTier(customer.getTongChiTieu() != null ? customer.getTongChiTieu() : 0.0, rule);
        double multiplier = resolveMultiplier(tier, rule);
        
        return (int) Math.floor(basePoints * multiplier);
    }

    private HangThanhVien resolveTier(Double totalSpending, com.ota.travi.entity.LoyaltyRule rule) {
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

    private double resolveMultiplier(HangThanhVien tier, com.ota.travi.entity.LoyaltyRule rule) {
        return switch (tier) {
            case DONG -> 1.0;
            case BAC -> rule.getSilverMultiplier() != null ? rule.getSilverMultiplier() : 1.0;
            case VANG -> rule.getGoldMultiplier() != null ? rule.getGoldMultiplier() : 1.0;
            case KIM_CUONG -> rule.getDiamondMultiplier() != null ? rule.getDiamondMultiplier() : 1.0;
        };
    }

    private BigDecimal thresholdFor(HangThanhVien tier, com.ota.travi.entity.LoyaltyRule rule) {
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
            com.ota.travi.entity.LoyaltyRule rule) {
        
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

    private int defaultPoints(KhachHang customer) {
        return customer.getDiemThanhVien() != null ? customer.getDiemThanhVien() : 0;
    }

    private int defaultUsedQuantity(Voucher voucher) {
        return voucher.getSoLuongDaDung() != null ? voucher.getSoLuongDaDung() : 0;
    }

    private BigDecimal currentSpending(KhachHang customer) {
        return BigDecimal.valueOf(customer.getTongChiTieu() != null ? customer.getTongChiTieu() : 0.0);
    }
}
