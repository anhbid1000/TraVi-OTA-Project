package com.ota.travi.service;

import com.ota.travi.dto.request.PromotionCreateRequest;
import com.ota.travi.dto.request.VoucherCreateRequest;
import com.ota.travi.dto.response.PromotionResponse;
import com.ota.travi.dto.response.VoucherApplyResponse;
import com.ota.travi.entity.HoSoKinhDoanh;
import com.ota.travi.entity.KhachHang;
import com.ota.travi.entity.KhuyenMaiTrucTiep;
import com.ota.travi.entity.LichSuDiem;
import com.ota.travi.entity.UuDai;
import com.ota.travi.entity.Voucher;
import com.ota.travi.enums.CreatedByRole;
import com.ota.travi.enums.HangThanhVien;
import com.ota.travi.enums.LoaiGiamGia;
import com.ota.travi.enums.LoaiGiaoDichDiem;
import com.ota.travi.enums.PhamViApDung;
import com.ota.travi.enums.TargetType;
import com.ota.travi.enums.TrangThaiUuDai;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Transactional
    public PromotionResponse createDirectPromotion(
            PromotionCreateRequest request,
            String createdByUserId,
            Boolean isAdmin) {

        validateDateRange(request.ngayBatDau(), request.ngayKetThuc());
        validatePartnerPromotionScope(request, createdByUserId, isAdmin);

        LoaiGiamGia loaiGiamGia = parseLoaiGiamGia(request.loaiGiamGia());
        validateDiscountRules(loaiGiamGia, request.mucGiam(), request.giaTriGiamToiDa());

        TargetType targetType = parseTargetType(request.targetType());
        checkOverlapPromotions(
                targetType,
                request.targetId(),
                request.ngayBatDau(),
                request.ngayKetThuc()
        );

        KhuyenMaiTrucTiep promotion = new KhuyenMaiTrucTiep();
        populateBasePromotionFields(
                promotion,
                request.tenUuDai(),
                request.moTa(),
                request.businessProfileId(),
                request.mucGiam(),
                loaiGiamGia,
                request.giaTriGiamToiDa(),
                request.ngayBatDau(),
                request.ngayKetThuc(),
                createdByUserId,
                isAdmin
        );
        promotion.setTargetType(targetType);
        promotion.setTargetId(request.targetId() != null ? String.valueOf(request.targetId()) : null);

        return toResponse(uuDaiRepository.save(promotion));
    }

    @Transactional
    public PromotionResponse createVoucher(
            VoucherCreateRequest request,
            String createdByUserId,
            Boolean isAdmin) {

        validateDateRange(request.ngayBatDau(), request.ngayKetThuc());
        validatePartnerVoucherScope(request, createdByUserId, isAdmin);

        LoaiGiamGia loaiGiamGia = parseLoaiGiamGia(request.loaiGiamGia());
        validateDiscountRules(loaiGiamGia, request.mucGiam(), request.giaTriGiamToiDa());

        if (request.soLuongPhatHanh() == null || request.soLuongPhatHanh() <= 0) {
            throw new ValidationException("Số lượng phát hành phải lớn hơn 0");
        }
        int usageLimitPerUser = request.usageLimitPerUser() == null ? 1 : request.usageLimitPerUser();
        if (usageLimitPerUser <= 0) {
            throw new ValidationException("Giới hạn sử dụng mỗi khách phải lớn hơn 0");
        }
        if (request.donHangToiThieu() != null && request.donHangToiThieu() < 0) {
            throw new ValidationException("Đơn hàng tối thiểu không được âm");
        }
        if (Boolean.TRUE.equals(request.choPhepDoiBangDiem())
                && (request.diemCanDoi() == null || request.diemCanDoi() <= 0)) {
            throw new ValidationException("Voucher đổi điểm phải có điểm cần đổi lớn hơn 0");
        }

        if (voucherRepository.existsByMaVoucherIgnoreCase(request.maVoucher())) {
            throw new BusinessConflictException("Mã voucher đã tồn tại");
        }

        Voucher voucher = new Voucher();
        populateBasePromotionFields(
                voucher,
                request.tenUuDai(),
                request.moTa(),
                request.businessProfileId(),
                request.mucGiam(),
                loaiGiamGia,
                request.giaTriGiamToiDa(),
                request.ngayBatDau(),
                request.ngayKetThuc(),
                createdByUserId,
                isAdmin
        );

        voucher.setMaVoucher(request.maVoucher());
        voucher.setSoLuongPhatHanh(request.soLuongPhatHanh());
        voucher.setSoLuongDaDung(0);
        voucher.setDonHangToiThieu(request.donHangToiThieu() != null ? request.donHangToiThieu() : 0.0);
        voucher.setUsageLimitPerUser(usageLimitPerUser);
        voucher.setDiemCanDoi(request.diemCanDoi() != null ? request.diemCanDoi() : 0);
        voucher.setChoPhepDoiBangDiem(request.choPhepDoiBangDiem() != null && request.choPhepDoiBangDiem());
        voucher.setPhamViApDung(parsePhamViApDung(request.phamViApDung()));

        return toResponse(voucherRepository.save(voucher));
    }

    @Transactional(readOnly = true)
    public List<PromotionResponse> getPromotionsByPartner(String partnerId) {
        return uuDaiRepository.findByCreatedByUserIdAndDeletedFalse(partnerId).stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PromotionResponse> getAllPromotions() {
        return uuDaiRepository.findByDeletedFalseAndTrangThaiUuDaiIn(
                        List.of(TrangThaiUuDai.DA_LEN_LICH, TrangThaiUuDai.DANG_CO_HIEU_LUC))
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PromotionResponse getPromotionById(Long id) {
        UuDai promotion = uuDaiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ưu đãi không tìm thấy"));
        if (promotion.getDeleted()) {
            throw new ResourceNotFoundException("Ưu đãi không tìm thấy");
        }
        return toResponse(promotion);
    }

    @Transactional
    public PromotionResponse pausePromotion(Long id, String actorId, Boolean isAdmin) {
        UuDai promotion = uuDaiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ưu đãi không tìm thấy"));
        requireAccessiblePromotion(promotion, actorId, isAdmin);
        if (!TrangThaiUuDai.DANG_CO_HIEU_LUC.equals(promotion.getTrangThaiUuDai())) {
            throw new ValidationException("Chỉ có thể tạm dừng ưu đãi đang hoạt động");
        }
        promotion.setTrangThaiUuDai(TrangThaiUuDai.TAM_DUNG);
        return toResponse(uuDaiRepository.save(promotion));
    }

    @Transactional
    public PromotionResponse resumePromotion(Long id, String actorId, Boolean isAdmin) {
        UuDai promotion = uuDaiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ưu đãi không tìm thấy"));
        requireAccessiblePromotion(promotion, actorId, isAdmin);
        if (!TrangThaiUuDai.TAM_DUNG.equals(promotion.getTrangThaiUuDai())) {
            throw new ValidationException("Chỉ có thể tiếp tục ưu đãi đã tạm dừng");
        }
        promotion.setTrangThaiUuDai(resolveActiveStatus(
                promotion.getNgayBatDau().atStartOfDay(),
                promotion.getNgayKetThuc().atTime(23, 59, 59)
        ));
        if (promotion.getTrangThaiUuDai() == TrangThaiUuDai.DA_HET_HAN) {
            throw new ValidationException("Không thể kích hoạt lại ưu đãi đã hết hạn");
        }
        return toResponse(uuDaiRepository.save(promotion));
    }

    @Transactional
    public void softDeletePromotion(Long id, String actorId, Boolean isAdmin) {
        UuDai promotion = uuDaiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ưu đãi không tìm thấy"));
        requireAccessiblePromotion(promotion, actorId, isAdmin);
        if (Boolean.TRUE.equals(promotion.getDeleted())) {
            return;
        }
        promotion.setDeleted(true);
        promotion.setTrangThaiUuDai(TrangThaiUuDai.DA_XOA);
        uuDaiRepository.save(promotion);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void autoUpdateExpiredPromotions() {
        int updatedRows = uuDaiRepository.updateStatusForExpiredPromotions(
                TrangThaiUuDai.DA_HET_HAN,
                LocalDate.now(),
                List.of(TrangThaiUuDai.DA_LEN_LICH, TrangThaiUuDai.DANG_CO_HIEU_LUC)
        );
        log.info("Expired promotions updated: {}", updatedRows);
    }

    @Transactional(readOnly = true)
    public VoucherApplyResponse applyVoucher(String voucherCode, Double originalTotal) {
        Voucher voucher = voucherRepository.findByMaVoucher(voucherCode)
                .orElseThrow(() -> new ResourceNotFoundException("Mã voucher không hợp lệ"));

        if (voucher.getDeleted() || voucher.getTrangThaiUuDai() != TrangThaiUuDai.DANG_CO_HIEU_LUC) {
            throw new ValidationException("Voucher không khả dụng");
        }
        if (defaultUsedQuantity(voucher) >= voucher.getSoLuongPhatHanh()) {
            throw new ValidationException("Voucher đã hết");
        }
        if (originalTotal < (voucher.getDonHangToiThieu() != null ? voucher.getDonHangToiThieu() : 0)) {
            throw new ValidationException("Đơn hàng chưa đạt mức tối thiểu");
        }

        double discountAmount = calculateDiscountAmount(originalTotal, voucher);
        double newTotal = Math.max(0, originalTotal - discountAmount);
        return new VoucherApplyResponse(voucherCode, discountAmount, originalTotal, newTotal, "Áp dụng voucher thành công");
    }

    @Transactional
    public void accumulatePoints(String customerId, Double bookingAmount, Long bookingId) {
        KhachHang customer = khachHangRepository.findByIdForUpdate(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Khách hàng không tìm thấy"));

        var activeRule = loyaltyRuleService.requireActiveRule();

        int earnedPoints = calculatePointsEarned(bookingAmount, customer, activeRule);
        int previousPoints = defaultPoints(customer);
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
        history.setBookingId(bookingId != null ? bookingId.toString() : null);
        history.setGhiChu("Tích điểm từ đơn đặt có giá trị: " + bookingAmount);
        lichSuDiemRepository.save(history);
    }

    private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new ValidationException("Ngày bắt đầu và ngày kết thúc không được để trống");
        }
        if (!endDate.isAfter(startDate)) {
            throw new ValidationException("Ngày kết thúc phải sau ngày bắt đầu");
        }
    }

    private void validateDiscountRules(LoaiGiamGia loaiGiamGia, Double mucGiam, Double giaTriGiamToiDa) {
        if (mucGiam == null || mucGiam <= 0) {
            throw new ValidationException("Mức giảm phải lớn hơn 0");
        }
        if (loaiGiamGia == LoaiGiamGia.PHAN_TRAM && mucGiam > 100) {
            throw new ValidationException("Mức giảm phần trăm không được vượt quá 100");
        }
        if (giaTriGiamToiDa != null && giaTriGiamToiDa <= 0) {
            throw new ValidationException("Giá trị giảm tối đa phải lớn hơn 0");
        }
    }

    private void validatePartnerPromotionScope(
            PromotionCreateRequest request,
            String partnerId,
            Boolean isAdmin) {
        HoSoKinhDoanh businessProfile = hoSoKinhDoanhRepository.findById(request.businessProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Hồ sơ kinh doanh không tìm thấy"));

        if (!isAdmin && !businessProfile.getDoiTac().getId().equals(partnerId)) {
            throw new ForbiddenOperationException("Bạn không có quyền tạo ưu đãi cho hồ sơ này");
        }
    }

    private void validatePartnerVoucherScope(
            VoucherCreateRequest request,
            String partnerId,
            Boolean isAdmin) {
        HoSoKinhDoanh businessProfile = hoSoKinhDoanhRepository.findById(request.businessProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Hồ sơ kinh doanh không tìm thấy"));

        if (!isAdmin && !businessProfile.getDoiTac().getId().equals(partnerId)) {
            throw new ForbiddenOperationException("Bạn không có quyền tạo voucher cho hồ sơ này");
        }
    }

    private void checkOverlapPromotions(
            TargetType targetType,
            Long targetId,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        if (targetType == null || targetId == null) {
            return;
        }

        boolean hasOverlap = uuDaiRepository.existsOverlappingPromotion(
                targetType,
                String.valueOf(targetId),
                startDate.toLocalDate(),
                endDate.toLocalDate(),
                List.of(TrangThaiUuDai.DA_LEN_LICH, TrangThaiUuDai.DANG_CO_HIEU_LUC)
        );

        if (hasOverlap) {
            throw new BusinessConflictException("Đã tồn tại ưu đãi trùng lịch");
        }
    }

    private void requireAccessiblePromotion(UuDai promotion, String actorId, Boolean isAdmin) {
        if (!isAdmin && !promotion.getCreatedByUserId().equals(actorId)) {
            throw new ForbiddenOperationException("Bạn không có quyền chỉnh sửa ưu đãi này");
        }
    }

    private PromotionResponse toResponse(UuDai uuDai) {
        String maVoucher = null;
        Integer soLuongPhatHanh = null;
        Integer soLuongDaDung = null;
        Double donHangToiThieu = null;
        Integer usageLimitPerUser = null;
        Integer diemCanDoi = null;
        Boolean choPhepDoiBangDiem = null;
        String phamViApDung = null;
        String targetType = null;
        Long targetId = null;
        String loaiUuDai = "DIRECT_PROMOTION";

        if (uuDai instanceof Voucher voucher) {
            maVoucher = voucher.getMaVoucher();
            soLuongPhatHanh = voucher.getSoLuongPhatHanh();
            soLuongDaDung = voucher.getSoLuongDaDung();
            donHangToiThieu = voucher.getDonHangToiThieu();
            usageLimitPerUser = voucher.getUsageLimitPerUser();
            diemCanDoi = voucher.getDiemCanDoi();
            choPhepDoiBangDiem = voucher.getChoPhepDoiBangDiem();
            phamViApDung = voucher.getPhamViApDung() != null ? voucher.getPhamViApDung().name() : null;
            loaiUuDai = "VOUCHER";
        }

        if (uuDai instanceof KhuyenMaiTrucTiep directPromotion) {
            targetType = directPromotion.getTargetType() != null ? directPromotion.getTargetType().name() : null;
            targetId = parseLongSafely(directPromotion.getTargetId());
        }

        return new PromotionResponse(
                uuDai.getId(),
                uuDai.getTenUuDai(),
                uuDai.getMoTa(),
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
                targetType,
                targetId,
                uuDai.getCreatedAt(),
                uuDai.getUpdatedAt()
        );
    }

    private TrangThaiUuDai resolveActiveStatus(LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startDate)) {
            return TrangThaiUuDai.DA_LEN_LICH;
        }
        if (now.isAfter(endDate)) {
            return TrangThaiUuDai.DA_HET_HAN;
        }
        return TrangThaiUuDai.DANG_CO_HIEU_LUC;
    }

    private double calculateDiscountAmount(Double originalTotal, Voucher voucher) {
        double discount;
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

        int basePoints = amount.divide(moneyPerPoint, 0, RoundingMode.DOWN).intValue();
        HangThanhVien tier = resolveTier(customer.getTongChiTieu() != null ? customer.getTongChiTieu() : 0.0, rule);
        double multiplier = resolveMultiplier(tier, rule);
        return (int) Math.floor(basePoints * multiplier);
    }

    private HangThanhVien resolveTier(Double totalSpending, com.ota.travi.entity.LoyaltyRule rule) {
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

    private double resolveMultiplier(HangThanhVien tier, com.ota.travi.entity.LoyaltyRule rule) {
        return switch (tier) {
            case BAC -> rule.getSilverMultiplier() != null ? rule.getSilverMultiplier() : 1.0;
            case VANG -> rule.getGoldMultiplier() != null ? rule.getGoldMultiplier() : 1.0;
            case KIM_CUONG -> rule.getDiamondMultiplier() != null ? rule.getDiamondMultiplier() : 1.0;
            default -> 1.0;
        };
    }

    private int defaultPoints(KhachHang customer) {
        return customer.getDiemThanhVien() != null ? customer.getDiemThanhVien() : 0;
    }

    private int defaultUsedQuantity(Voucher voucher) {
        return voucher.getSoLuongDaDung() != null ? voucher.getSoLuongDaDung() : 0;
    }

    private LoaiGiamGia parseLoaiGiamGia(String value) {
        try {
            return LoaiGiamGia.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Loại giảm giá không hợp lệ");
        }
    }

    private TargetType parseTargetType(String value) {
        if (value == null || value.isBlank()) {
            return TargetType.ALL_PLATFORM;
        }
        try {
            return TargetType.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Target type không hợp lệ");
        }
    }

    private PhamViApDung parsePhamViApDung(String value) {
        try {
            return PhamViApDung.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Phạm vi áp dụng không hợp lệ");
        }
    }

    private Long parseLongSafely(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void populateBasePromotionFields(
            UuDai promotion,
            String tenUuDai,
            String moTa,
            String businessProfileId,
            Double mucGiam,
            LoaiGiamGia loaiGiamGia,
            Double giaTriGiamToiDa,
            LocalDateTime ngayBatDau,
            LocalDateTime ngayKetThuc,
            String createdByUserId,
            Boolean isAdmin
    ) {
        promotion.setTenUuDai(tenUuDai);
        promotion.setMoTa(moTa);
        promotion.setCreatedByUserId(createdByUserId);
        promotion.setCreatedByRole(Boolean.TRUE.equals(isAdmin) ? CreatedByRole.QUAN_TRI_VIEN : CreatedByRole.DOI_TAC);
        promotion.setBusinessProfileId(businessProfileId);
        promotion.setMucGiam(mucGiam);
        promotion.setLoaiGiamGia(loaiGiamGia);
        promotion.setGiaTriGiamToiDa(giaTriGiamToiDa);
        promotion.setNgayBatDau(ngayBatDau.toLocalDate());
        promotion.setNgayKetThuc(ngayKetThuc.toLocalDate());
        promotion.setDeleted(false);
        promotion.setTrangThaiUuDai(resolveActiveStatus(ngayBatDau, ngayKetThuc));
    }
}
