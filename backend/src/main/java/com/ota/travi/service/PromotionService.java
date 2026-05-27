package com.ota.travi.service;


import com.ota.travi.dto.request.PromotionCreateRequest;
import com.ota.travi.dto.request.VoucherCreateRequest;
import com.ota.travi.dto.response.PromotionResponse;
import com.ota.travi.entity.KhuyenMaiTrucTiep;
import com.ota.travi.entity.UuDai;
import com.ota.travi.entity.Voucher;
import com.ota.travi.enums.CreatedByRole;
import com.ota.travi.enums.TargetType;
import com.ota.travi.enums.TrangThaiUuDai;
import com.ota.travi.exception.BusinessConflictException;
import com.ota.travi.exception.BusinessException;
import com.ota.travi.exception.ForbiddenOperationException;
import com.ota.travi.exception.ResourceNotFoundException;
import com.ota.travi.repository.HoSoKinhDoanhRepository;
import com.ota.travi.repository.KhuyenMaiTrucTiepRepository;
import com.ota.travi.repository.KhachSanRepository;
import com.ota.travi.repository.MonAnRepository;
import com.ota.travi.repository.NhaHangRepository;
import com.ota.travi.repository.PhongRepository;
import com.ota.travi.repository.UuDaiRepository;
import com.ota.travi.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionService {
    private final UuDaiRepository uuDaiRepository;
    private final KhuyenMaiTrucTiepRepository khuyenMaiTrucTiepRepository;
    private final VoucherRepository voucherRepository;
    private final HoSoKinhDoanhRepository hoSoKinhDoanhRepository;
    private final KhachSanRepository khachSanRepository;
    private final NhaHangRepository nhaHangRepository;
    private final PhongRepository phongRepository;
    private final MonAnRepository monAnRepository;

    @Transactional
    public PromotionResponse createDirectPromotion(PromotionCreateRequest request, String createdByUserId, boolean createdByAdmin) {
        validateDateRange(request.ngayBatDau(), request.ngayKetThuc());
        validatePartnerPromotionScope(request, createdByUserId, createdByAdmin);

        KhuyenMaiTrucTiep promotion = new KhuyenMaiTrucTiep();
        applyBaseFields(promotion, request, createdByUserId, createdByAdmin);
        promotion.setTargetType(request.targetType());
        promotion.setTargetId(resolveTargetId(request.targetType(), request.targetId()));

        return toResponse(khuyenMaiTrucTiepRepository.save(promotion));
    }

    @Transactional
    public PromotionResponse createVoucher(VoucherCreateRequest request, String createdByUserId, boolean createdByAdmin) {
        validateDateRange(request.ngayBatDau(), request.ngayKetThuc());
        validatePartnerVoucherScope(request, createdByUserId, createdByAdmin);
        if (voucherRepository.existsByMaVoucherIgnoreCase(request.maVoucher())) {
            throw new BusinessConflictException("Mã voucher đã tồn tại");
        }

        Voucher voucher = new Voucher();
        applyBaseFields(voucher, request, createdByUserId, createdByAdmin);
        voucher.setMaVoucher(request.maVoucher().trim());
        voucher.setSoLuongPhatHanh(request.soLuongPhatHanh());
        voucher.setDonHangToiThieu(defaultBigDecimal(request.donHangToiThieu()));
        voucher.setUsageLimitPerUser(request.usageLimitPerUser() == null ? 1 : request.usageLimitPerUser());
        voucher.setDiemCanDoi(request.diemCanDoi() == null ? 0 : request.diemCanDoi());
        voucher.setChoPhepDoiBangDiem(Boolean.TRUE.equals(request.choPhepDoiBangDiem()));
        voucher.setPhamViApDung(request.phamViApDung());

        return toResponse(voucherRepository.save(voucher));
    }

    @Transactional(readOnly = true)
    public List<PromotionResponse> getPromotionsByPartner(String partnerId) {
        return uuDaiRepository.findByCreatedByUserIdAndDeletedFalseOrderByCreatedAtDesc(partnerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PromotionResponse> getAllPromotions() {
        return uuDaiRepository.findByDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PromotionResponse pausePromotion(Long id, String actorId, boolean actorIsAdmin) {
        UuDai promotion = requireAccessiblePromotion(id, actorId, actorIsAdmin);
        promotion.setTrangThaiUuDai(TrangThaiUuDai.TAM_DUNG);
        promotion.setUpdatedAt(LocalDateTime.now());
        return toResponse(uuDaiRepository.save(promotion));
    }

    @Transactional
    public PromotionResponse resumePromotion(Long id, String actorId, boolean actorIsAdmin) {
        UuDai promotion = requireAccessiblePromotion(id, actorId, actorIsAdmin);
        promotion.setTrangThaiUuDai(resolveActiveStatus(promotion));
        promotion.setUpdatedAt(LocalDateTime.now());
        return toResponse(uuDaiRepository.save(promotion));
    }

    private void applyBaseFields(UuDai promotion, PromotionCreateRequest request, String createdByUserId, boolean createdByAdmin) {
        promotion.setTenUuDai(request.tenUuDai().trim());
        promotion.setMoTa(request.moTa());
        promotion.setCreatedByUserId(createdByUserId);
        promotion.setCreatedByRole(createdByAdmin ? CreatedByRole.QUAN_TRI_VIEN : CreatedByRole.DOI_TAC);
        promotion.setBusinessProfileId(normalizeNullable(request.businessProfileId()));
        promotion.setMucGiam(request.mucGiam());
        promotion.setLoaiGiamGia(request.loaiGiamGia());
        promotion.setGiaTriGiamToiDa(request.giaTriGiamToiDa());
        promotion.setNgayBatDau(request.ngayBatDau());
        promotion.setNgayKetThuc(request.ngayKetThuc());
        promotion.setTrangThaiUuDai(resolveActiveStatus(request.ngayBatDau(), request.ngayKetThuc()));
    }

    private void applyBaseFields(UuDai promotion, VoucherCreateRequest request, String createdByUserId, boolean createdByAdmin) {
        promotion.setTenUuDai(request.tenUuDai().trim());
        promotion.setMoTa(request.moTa());
        promotion.setCreatedByUserId(createdByUserId);
        promotion.setCreatedByRole(createdByAdmin ? CreatedByRole.QUAN_TRI_VIEN : CreatedByRole.DOI_TAC);
        promotion.setBusinessProfileId(normalizeNullable(request.businessProfileId()));
        promotion.setMucGiam(request.mucGiam());
        promotion.setLoaiGiamGia(request.loaiGiamGia());
        promotion.setGiaTriGiamToiDa(request.giaTriGiamToiDa());
        promotion.setNgayBatDau(request.ngayBatDau());
        promotion.setNgayKetThuc(request.ngayKetThuc());
        promotion.setTrangThaiUuDai(resolveActiveStatus(request.ngayBatDau(), request.ngayKetThuc()));
    }

    private UuDai requireAccessiblePromotion(Long id, String actorId, boolean actorIsAdmin) {
        UuDai promotion = uuDaiRepository.findById(id)
                .filter(item -> !item.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ưu đãi"));

        if (!actorIsAdmin && !actorId.equals(promotion.getCreatedByUserId())) {
            throw new ForbiddenOperationException("Bạn không có quyền thao tác ưu đãi này");
        }

        return promotion;
    }

    private void validatePartnerPromotionScope(PromotionCreateRequest request, String partnerId, boolean createdByAdmin) {
        if (createdByAdmin) {
            return;
        }

        String businessProfileId = requirePartnerBusinessProfileId(request.businessProfileId(), partnerId);
        switch (request.targetType()) {
            case ALL_PLATFORM -> {
                // The promotion is still scoped to the partner business profile validated above.
            }
            case HOTEL -> requireOwnedHotel(request.targetId(), businessProfileId);
            case RESTAURANT -> requireOwnedRestaurant(request.targetId(), businessProfileId);
            case ROOM -> requireOwnedRoom(request.targetId(), businessProfileId);
            case MENU_ITEM -> requireOwnedMenuItem(request.targetId(), businessProfileId);
        }
    }

    private void validatePartnerVoucherScope(VoucherCreateRequest request, String partnerId, boolean createdByAdmin) {
        if (createdByAdmin) {
            return;
        }

        requirePartnerBusinessProfileId(request.businessProfileId(), partnerId);
    }

    private String requirePartnerBusinessProfileId(String businessProfileId, String partnerId) {
        if (businessProfileId == null || businessProfileId.isBlank()) {
            throw new BusinessException("businessProfileId không được để trống với ưu đãi của đối tác", HttpStatus.BAD_REQUEST);
        }

        String normalizedBusinessProfileId = businessProfileId.trim();
        boolean owned = hoSoKinhDoanhRepository.existsByIdHoSoAndDoiTac_IdAndDeletedFalse(
                normalizedBusinessProfileId,
                partnerId
        );
        if (!owned) {
            throw new ForbiddenOperationException("Hồ sơ kinh doanh không thuộc đối tác hiện tại");
        }

        return normalizedBusinessProfileId;
    }

    private void requireOwnedHotel(String targetId, String businessProfileId) {
        String normalizedTargetId = requireTargetId(targetId);
        if (!khachSanRepository.existsByIdTaiSanAndHoSoKinhDoanh_IdHoSo(normalizedTargetId, businessProfileId)) {
            throw new ForbiddenOperationException("Khách sạn không thuộc hồ sơ kinh doanh đã chọn");
        }
    }

    private void requireOwnedRestaurant(String targetId, String businessProfileId) {
        String normalizedTargetId = requireTargetId(targetId);
        if (!nhaHangRepository.existsByIdTaiSanAndHoSoKinhDoanh_IdHoSo(normalizedTargetId, businessProfileId)) {
            throw new ForbiddenOperationException("Nhà hàng không thuộc hồ sơ kinh doanh đã chọn");
        }
    }

    private void requireOwnedRoom(String targetId, String businessProfileId) {
        String normalizedTargetId = requireTargetId(targetId);
        if (!phongRepository.existsByIdAndDeletedFalseAndKhachSan_HoSoKinhDoanh_IdHoSo(normalizedTargetId, businessProfileId)) {
            throw new ForbiddenOperationException("Phòng không thuộc hồ sơ kinh doanh đã chọn");
        }
    }

    private void requireOwnedMenuItem(String targetId, String businessProfileId) {
        String normalizedTargetId = requireTargetId(targetId);
        if (!monAnRepository.existsByIdAndDeletedFalseAndThucDon_NhaHang_HoSoKinhDoanh_IdHoSo(normalizedTargetId, businessProfileId)) {
            throw new ForbiddenOperationException("Món ăn không thuộc hồ sơ kinh doanh đã chọn");
        }
    }

    private String requireTargetId(String targetId) {
        if (targetId == null || targetId.isBlank()) {
            throw new BusinessException("targetId không được để trống với loại áp dụng này", HttpStatus.BAD_REQUEST);
        }
        return targetId.trim();
    }

    private String normalizeNullable(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private PromotionResponse toResponse(UuDai promotion) {
        Voucher voucher = promotion instanceof Voucher value ? value : null;
        KhuyenMaiTrucTiep directPromotion = promotion instanceof KhuyenMaiTrucTiep value ? value : null;

        return new PromotionResponse(
                promotion.getId(),
                promotion.getTenUuDai(),
                promotion.getMoTa(),
                promotion.getCreatedByUserId(),
                promotion.getCreatedByRole(),
                promotion.getBusinessProfileId(),
                promotion.getMucGiam(),
                promotion.getLoaiGiamGia(),
                promotion.getGiaTriGiamToiDa(),
                promotion.getNgayBatDau(),
                promotion.getNgayKetThuc(),
                promotion.getTrangThaiUuDai(),
                promotion.isDeleted(),
                voucher == null ? "DIRECT" : "VOUCHER",
                voucher == null ? null : voucher.getMaVoucher(),
                voucher == null ? null : voucher.getSoLuongPhatHanh(),
                voucher == null ? null : voucher.getSoLuongDaDung(),
                voucher == null ? null : voucher.getDonHangToiThieu(),
                voucher == null ? null : voucher.getUsageLimitPerUser(),
                voucher == null ? null : voucher.getDiemCanDoi(),
                voucher == null ? null : voucher.getChoPhepDoiBangDiem(),
                voucher == null ? null : voucher.getPhamViApDung(),
                directPromotion == null ? null : directPromotion.getTargetType(),
                directPromotion == null ? null : directPromotion.getTargetId(),
                promotion.getCreatedAt(),
                promotion.getUpdatedAt()
        );
    }

    private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (!endDate.isAfter(startDate)) {
            throw new BusinessException("Ngày kết thúc phải sau ngày bắt đầu", HttpStatus.BAD_REQUEST);
        }
    }

    private String resolveTargetId(TargetType targetType, String targetId) {
        if (targetType == TargetType.ALL_PLATFORM) {
            return TargetType.ALL_PLATFORM.name();
        }
        if (targetId != null && !targetId.isBlank()) {
            return targetId.trim();
        }
        throw new BusinessException("targetId không được để trống với loại áp dụng này", HttpStatus.BAD_REQUEST);
    }

    private TrangThaiUuDai resolveActiveStatus(UuDai promotion) {
        return resolveActiveStatus(promotion.getNgayBatDau(), promotion.getNgayKetThuc());
    }

    private TrangThaiUuDai resolveActiveStatus(LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();
        if (endDate.isBefore(now)) {
            return TrangThaiUuDai.DA_HET_HAN;
        }
        if (startDate.isAfter(now)) {
            return TrangThaiUuDai.DA_LEN_LICH;
        }
        return TrangThaiUuDai.DANG_CO_HIEU_LUC;
    }

    private BigDecimal defaultBigDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
