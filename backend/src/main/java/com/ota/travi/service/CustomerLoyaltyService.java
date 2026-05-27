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
import com.ota.travi.enums.TrangThaiCustomerVoucher;
import com.ota.travi.enums.TrangThaiUuDai;
import com.ota.travi.exception.BusinessException;
import com.ota.travi.exception.ResourceNotFoundException;
import com.ota.travi.repository.CustomerVoucherRepository;
import com.ota.travi.repository.KhachHangRepository;
import com.ota.travi.repository.LichSuDiemRepository;
import com.ota.travi.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerLoyaltyService {
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private final KhachHangRepository khachHangRepository;
    private final LoyaltyRuleService loyaltyRuleService;
    private final LichSuDiemRepository lichSuDiemRepository;
    private final CustomerVoucherRepository customerVoucherRepository;
    private final VoucherRepository voucherRepository;

    @Transactional(readOnly = true)
    public LoyaltySummaryResponse getLoyaltySummary(String khachHangId) {
        KhachHang khachHang = requireCustomer(khachHangId);
        LoyaltyRule rule = loyaltyRuleService.requireActiveRule();
        BigDecimal tongChiTieu = currentSpending(khachHang);
        HangThanhVien hangHienTai = resolveTier(tongChiTieu, rule);
        HangThanhVien hangTiepTheo = resolveNextTier(hangHienTai);
        BigDecimal nextThreshold = thresholdFor(hangTiepTheo, rule);
        BigDecimal soTienCanChiThem = nextThreshold == null
                ? BigDecimal.ZERO
                : nextThreshold.subtract(tongChiTieu).max(BigDecimal.ZERO);

        return new LoyaltySummaryResponse(
                khachHang.getId(),
                defaultPoints(khachHang),
                tongChiTieu,
                hangHienTai,
                hangTiepTheo,
                soTienCanChiThem,
                resolveProgressPercent(tongChiTieu, hangHienTai, rule),
                lichSuDiemRepository.findTop10ByKhachHang_IdOrderByCreatedAtDesc(khachHangId)
                        .stream()
                        .map(this::toPointHistoryResponse)
                        .toList(),
                customerVoucherRepository.findByKhachHang_IdAndTrangThaiOrderByIssuedAtDesc(
                                khachHangId,
                                TrangThaiCustomerVoucher.CHUA_DUNG
                        )
                        .stream()
                        .map(this::toCustomerVoucherResponse)
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public List<PromotionResponse> getExchangeableVouchers() {
        return voucherRepository.findExchangeableVouchers(TrangThaiUuDai.DANG_CO_HIEU_LUC)
                .stream()
                .map(this::toPromotionResponse)
                .toList();
    }

    @Transactional
    public ExchangeVoucherResponse exchangeVoucher(String khachHangId, Long voucherId) {
        KhachHang khachHang = requireCustomer(khachHangId);
        Voucher voucher = voucherRepository.findByIdForExchange(voucherId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy voucher"));

        validateVoucherExchangeable(voucher);

        int diemTruocGiaoDich = defaultPoints(khachHang);
        int diemCanDoi = voucher.getDiemCanDoi() == null ? 0 : voucher.getDiemCanDoi();
        if (diemTruocGiaoDich < diemCanDoi) {
            throw new BusinessException("Điểm hiện tại không đủ để đổi voucher", HttpStatus.BAD_REQUEST);
        }

        int diemSauGiaoDich = diemTruocGiaoDich - diemCanDoi;
        khachHang.setDiemThanhVien(diemSauGiaoDich);
        voucher.setSoLuongDaDung(defaultUsedQuantity(voucher) + 1);

        CustomerVoucher customerVoucher = new CustomerVoucher();
        customerVoucher.setKhachHang(khachHang);
        customerVoucher.setVoucher(voucher);
        customerVoucher.setMaVoucherCaNhan(buildPersonalVoucherCode(voucher, khachHang));
        customerVoucher.setTrangThai(TrangThaiCustomerVoucher.CHUA_DUNG);
        customerVoucher.setIssuedAt(LocalDateTime.now());
        customerVoucher.setExpiredAt(voucher.getNgayKetThuc());
        CustomerVoucher savedCustomerVoucher = customerVoucherRepository.save(customerVoucher);

        LichSuDiem pointHistory = new LichSuDiem();
        pointHistory.setKhachHang(khachHang);
        pointHistory.setSoDiemThayDoi(-diemCanDoi);
        pointHistory.setLoaiGiaoDichDiem(LoaiGiaoDichDiem.DOI_VOUCHER);
        pointHistory.setDiemTruocGiaoDich(diemTruocGiaoDich);
        pointHistory.setDiemSauGiaoDich(diemSauGiaoDich);
        pointHistory.setVoucherId(voucher.getId());
        pointHistory.setGhiChu("Đổi điểm lấy voucher " + voucher.getMaVoucher());
        lichSuDiemRepository.save(pointHistory);

        return new ExchangeVoucherResponse(
                savedCustomerVoucher.getId(),
                voucher.getId(),
                voucher.getMaVoucher(),
                savedCustomerVoucher.getMaVoucherCaNhan(),
                diemCanDoi,
                diemSauGiaoDich,
                "Đổi voucher thành công"
        );
    }

    private KhachHang requireCustomer(String khachHangId) {
        return khachHangRepository.findById(khachHangId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng"));
    }

    private void validateVoucherExchangeable(Voucher voucher) {
        if (voucher.isDeleted()
                || !Boolean.TRUE.equals(voucher.getChoPhepDoiBangDiem())
                || voucher.getTrangThaiUuDai() != TrangThaiUuDai.DANG_CO_HIEU_LUC) {
            throw new BusinessException("Voucher không thể đổi bằng điểm", HttpStatus.BAD_REQUEST);
        }
        if (defaultUsedQuantity(voucher) >= voucher.getSoLuongPhatHanh()) {
            throw new BusinessException("Voucher đã hết số lượng phát hành", HttpStatus.CONFLICT);
        }
        if (voucher.getDiemCanDoi() == null || voucher.getDiemCanDoi() < 0) {
            throw new BusinessException("Voucher chưa cấu hình điểm đổi hợp lệ", HttpStatus.BAD_REQUEST);
        }
    }

    private PointHistoryResponse toPointHistoryResponse(LichSuDiem pointHistory) {
        return new PointHistoryResponse(
                pointHistory.getId(),
                pointHistory.getKhachHang().getId(),
                pointHistory.getSoDiemThayDoi(),
                pointHistory.getLoaiGiaoDichDiem(),
                pointHistory.getDiemTruocGiaoDich(),
                pointHistory.getDiemSauGiaoDich(),
                pointHistory.getBookingId(),
                pointHistory.getVoucherId(),
                pointHistory.getGhiChu(),
                pointHistory.getCreatedAt()
        );
    }

    private CustomerVoucherResponse toCustomerVoucherResponse(CustomerVoucher customerVoucher) {
        Voucher voucher = customerVoucher.getVoucher() instanceof Voucher value ? value : null;
        return new CustomerVoucherResponse(
                customerVoucher.getId(),
                customerVoucher.getVoucher().getId(),
                customerVoucher.getKhachHang().getId(),
                voucher == null ? null : voucher.getMaVoucher(),
                customerVoucher.getMaVoucherCaNhan(),
                customerVoucher.getVoucher().getTenUuDai(),
                customerVoucher.getTrangThai(),
                customerVoucher.getIssuedAt(),
                customerVoucher.getUsedAt(),
                customerVoucher.getExpiredAt(),
                customerVoucher.getBookingId()
        );
    }

    private PromotionResponse toPromotionResponse(Voucher voucher) {
        return new PromotionResponse(
                voucher.getId(),
                voucher.getTenUuDai(),
                voucher.getMoTa(),
                voucher.getCreatedByUserId(),
                voucher.getCreatedByRole(),
                voucher.getBusinessProfileId(),
                voucher.getMucGiam(),
                voucher.getLoaiGiamGia(),
                voucher.getGiaTriGiamToiDa(),
                voucher.getNgayBatDau(),
                voucher.getNgayKetThuc(),
                voucher.getTrangThaiUuDai(),
                voucher.isDeleted(),
                "VOUCHER",
                voucher.getMaVoucher(),
                voucher.getSoLuongPhatHanh(),
                voucher.getSoLuongDaDung(),
                voucher.getDonHangToiThieu(),
                voucher.getUsageLimitPerUser(),
                voucher.getDiemCanDoi(),
                voucher.getChoPhepDoiBangDiem(),
                voucher.getPhamViApDung(),
                null,
                null,
                voucher.getCreatedAt(),
                voucher.getUpdatedAt()
        );
    }

    private HangThanhVien resolveTier(BigDecimal tongChiTieu, LoyaltyRule rule) {
        if (tongChiTieu.compareTo(rule.getDiamondThreshold()) >= 0) {
            return HangThanhVien.KIM_CUONG;
        }
        if (tongChiTieu.compareTo(rule.getGoldThreshold()) >= 0) {
            return HangThanhVien.VANG;
        }
        if (tongChiTieu.compareTo(rule.getSilverThreshold()) >= 0) {
            return HangThanhVien.BAC;
        }
        return HangThanhVien.DONG;
    }

    private HangThanhVien resolveNextTier(HangThanhVien currentTier) {
        return switch (currentTier) {
            case DONG -> HangThanhVien.BAC;
            case BAC -> HangThanhVien.VANG;
            case VANG -> HangThanhVien.KIM_CUONG;
            case KIM_CUONG -> null;
        };
    }

    private BigDecimal thresholdFor(HangThanhVien tier, LoyaltyRule rule) {
        if (tier == null) {
            return null;
        }
        return switch (tier) {
            case BAC -> rule.getSilverThreshold();
            case VANG -> rule.getGoldThreshold();
            case KIM_CUONG -> rule.getDiamondThreshold();
            case DONG -> BigDecimal.ZERO;
        };
    }

    private BigDecimal resolveProgressPercent(BigDecimal tongChiTieu, HangThanhVien currentTier, LoyaltyRule rule) {
        if (currentTier == HangThanhVien.KIM_CUONG) {
            return ONE_HUNDRED;
        }

        BigDecimal currentThreshold = thresholdFor(currentTier, rule);
        BigDecimal nextThreshold = thresholdFor(resolveNextTier(currentTier), rule);
        if (nextThreshold == null || nextThreshold.compareTo(currentThreshold) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal progress = tongChiTieu.subtract(currentThreshold)
                .max(BigDecimal.ZERO)
                .divide(nextThreshold.subtract(currentThreshold), 4, RoundingMode.HALF_UP)
                .multiply(ONE_HUNDRED);
        return progress.min(ONE_HUNDRED).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal currentSpending(KhachHang khachHang) {
        return BigDecimal.valueOf(khachHang.getTongChiTieu() == null ? 0.0 : khachHang.getTongChiTieu());
    }

    private int defaultPoints(KhachHang khachHang) {
        return khachHang.getDiemThanhVien() == null ? 0 : khachHang.getDiemThanhVien();
    }

    private int defaultUsedQuantity(Voucher voucher) {
        return voucher.getSoLuongDaDung() == null ? 0 : voucher.getSoLuongDaDung();
    }

    private String buildPersonalVoucherCode(Voucher voucher, KhachHang khachHang) {
        String customerSuffix = khachHang.getId().length() <= 8
                ? khachHang.getId()
                : khachHang.getId().substring(khachHang.getId().length() - 8);
        return voucher.getMaVoucher() + "-" + customerSuffix + "-" + System.currentTimeMillis();
    }
}
