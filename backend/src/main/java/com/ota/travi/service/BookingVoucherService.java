package com.ota.travi.service;

import com.ota.travi.entity.CustomerVoucher;
import com.ota.travi.entity.DonDatCho;
import com.ota.travi.entity.Voucher;
import com.ota.travi.enums.LoaiGiamGia;
import com.ota.travi.enums.TrangThaiDon;
import com.ota.travi.enums.TrangThaiCustomerVoucher;
import com.ota.travi.enums.TrangThaiUuDai;
import com.ota.travi.exception.BusinessConflictException;
import com.ota.travi.exception.ResourceNotFoundException;
import com.ota.travi.exception.ValidationException;
import com.ota.travi.repository.CustomerVoucherRepository;
import com.ota.travi.repository.DonDatChoRepository;
import com.ota.travi.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookingVoucherService {

    private final VoucherRepository voucherRepository;
    private final CustomerVoucherRepository customerVoucherRepository;
    private final DonDatChoRepository donDatChoRepository;
    private final VoucherReserveService voucherReserveService;
    private final NotificationEventService notificationEventService;
    private final PromotionAnalyticsService promotionAnalyticsService;

    public VoucherCheckoutResponse applyVoucherToBooking(String bookingId, String customerId, String maVoucher) {
        log.info("Applying voucher {} to booking {} for customer {}", maVoucher, bookingId, customerId);

        DonDatCho booking = donDatChoRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getKhachHang().getId().equals(customerId)) {
            throw new BusinessConflictException("Customer does not own this booking");
        }

        ensureBookingIsPayable(booking);

        if (booking.getVoucherId() != null) {
            throw new BusinessConflictException("Booking already has a voucher applied");
        }

        Voucher voucher = voucherRepository.findByMaVoucher(maVoucher)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with code: " + maVoucher));

        validateVoucherEligibility(voucher, customerId, booking);

        CustomerVoucher customerVoucher = findAvailableCustomerVoucher(customerId, voucher.getId());
        voucherReserveService.reserveVoucher(customerId, customerVoucher.getId(), 1800);

        Double originalAmount = normalizeMoney(booking.getTongTienGoc());
        Double discount = calculateDiscount(voucher, originalAmount);

        // Keep customer voucher id so payment callback can consume/release this exact reserve.
        booking.setVoucherId(customerVoucher.getId());
        booking.setTienKhuyenMai(discount);
        booking.setTongTienThanhToan(Math.max(0.0, originalAmount - discount));

        DonDatCho updatedBooking = donDatChoRepository.save(booking);

        notificationEventService.emitVoucherApplied(customerId, voucher.getId(), discount);
        promotionAnalyticsService.trackVoucherUsage(voucher.getId(), customerId);

        log.info("Voucher {} successfully applied to booking {}", maVoucher, bookingId);

        return VoucherCheckoutResponse.builder()
                .bookingId(bookingId)
                .voucherId(voucher.getId())
                .maVoucher(maVoucher)
                .originalAmount(new BigDecimal(booking.getTongTienGoc()))
                .discountAmount(new BigDecimal(discount))
                .finalAmount(new BigDecimal(updatedBooking.getTongTienThanhToan()))
                .isApplied(true)
                .message("Voucher applied successfully")
                .build();
    }

    @Transactional
    public void removeVoucherFromBooking(String bookingId, String customerId) {
        log.info("Removing voucher from booking {} for customer {}", bookingId, customerId);

        DonDatCho booking = donDatChoRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));     

        if (!booking.getKhachHang().getId().equals(customerId)) {
            throw new BusinessConflictException("Customer does not own this booking");
        }

        ensureBookingIsPayable(booking);

        if (booking.getVoucherId() == null) {
            throw new BusinessConflictException("Booking does not have a voucher applied");
        }

        Long customerVoucherId = booking.getVoucherId();
        CustomerVoucher customerVoucher = customerVoucherRepository.findById(customerVoucherId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer voucher not found"));

        if (!customerVoucher.getCustomerId().equals(customerId)) {
            throw new BusinessConflictException("Customer does not own this booking voucher");
        }

        if (customerVoucher.getTrangThai() == TrangThaiCustomerVoucher.RESERVED) {
            voucherReserveService.releaseVoucher(customerVoucher.getId());
        } else if (customerVoucher.getTrangThai() != TrangThaiCustomerVoucher.CHUA_DUNG) {
            throw new BusinessConflictException("Booking voucher is not releasable in current status");
        }

        booking.setVoucherId(null);
        booking.setTienKhuyenMai(0.0);
        booking.setTongTienThanhToan(normalizeMoney(booking.getTongTienGoc()));

        donDatChoRepository.save(booking);

        notificationEventService.emitVoucherReleased(customerId, customerVoucher.getVoucherId());

        log.info("Voucher successfully removed from booking {}", bookingId);
    }

    @Transactional(readOnly = true)
    public List<VoucherPreviewResponse> getAvailableVouchersForBooking(String bookingId, String customerId) {
        log.debug("Fetching available vouchers for booking {} and customer {}", bookingId, customerId);

        DonDatCho booking = donDatChoRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));     

        if (!booking.getKhachHang().getId().equals(customerId)) {
            throw new BusinessConflictException("Customer does not own this booking");
        }

        List<Voucher> activeVouchers = voucherRepository.findByTrangThai(TrangThaiUuDai.DANG_CO_HIEU_LUC);
        Double originalAmount = normalizeMoney(booking.getTongTienGoc());

        return activeVouchers.stream()
                .map(voucher -> {
                    try {
                        validateVoucherEligibility(voucher, customerId, booking);
                        Double discount = calculateDiscount(voucher, originalAmount);

                        return VoucherPreviewResponse.builder()
                                .voucherId(voucher.getId())
                                .maVoucher(voucher.getMaVoucher())
                                .discountAmount(new BigDecimal(discount))
                                .discountPercentage(getDiscountPercentage(voucher))
                                .estimatedFinalAmount(new BigDecimal(Math.max(0.0, originalAmount - discount)))
                                .isEligible(true)
                                .build();
                    } catch (Exception e) {
                        return VoucherPreviewResponse.builder()
                                .voucherId(voucher.getId())
                                .maVoucher(voucher.getMaVoucher())
                                .isEligible(false)
                                .ineligibilityReason(e.getMessage())
                                .build();
                    }
                })
                .sorted((a, b) -> {
                    if (!a.getIsEligible() && b.getIsEligible()) return 1;
                    if (a.getIsEligible() && !b.getIsEligible()) return -1;
                    if (a.getIsEligible() && b.getIsEligible()) {
                        return b.getDiscountAmount().compareTo(a.getDiscountAmount());
                    }
                    return 0;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public VoucherPreviewResponse previewVoucherDiscount(String bookingId, String maVoucher) {
        log.debug("Previewing voucher {} for booking {}", maVoucher, bookingId);

        DonDatCho booking = donDatChoRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));     

        Voucher voucher = voucherRepository.findByMaVoucher(maVoucher)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with code: " + maVoucher));   

        try {
            Double originalAmount = normalizeMoney(booking.getTongTienGoc());
            Double discount = calculateDiscount(voucher, originalAmount);

            return VoucherPreviewResponse.builder()
                    .voucherId(voucher.getId())
                    .maVoucher(voucher.getMaVoucher())
                    .discountAmount(new BigDecimal(discount))
                    .discountPercentage(getDiscountPercentage(voucher))
                    .estimatedFinalAmount(new BigDecimal(Math.max(0.0, originalAmount - discount)))
                    .isEligible(true)
                    .build();
        } catch (Exception e) {
            return VoucherPreviewResponse.builder()
                    .voucherId(voucher.getId())
                    .maVoucher(voucher.getMaVoucher())
                    .isEligible(false)
                    .ineligibilityReason(e.getMessage())
                    .build();
        }
    }

    private void validateVoucherEligibility(Voucher voucher, String customerId, DonDatCho booking) {
        log.debug("Validating voucher {} eligibility for customer {}", voucher.getId(), customerId);

        if (voucher.getTrangThaiUuDai() != TrangThaiUuDai.DANG_CO_HIEU_LUC) {
            throw new ValidationException("Voucher is not active");
        }

        LocalDate today = LocalDate.now();
        if (voucher.getNgayBatDau() != null && voucher.getNgayBatDau().isAfter(today)) {
            throw new ValidationException("Voucher is not active yet");
        }
        if (voucher.getNgayKetThuc() != null && voucher.getNgayKetThuc().isBefore(today)) {
            throw new ValidationException("Voucher has expired");
        }

        if (voucher.getDonHangToiThieu() != null &&
            booking.getTongTienGoc() != null &&
            booking.getTongTienGoc() < voucher.getDonHangToiThieu()) {
            throw new ValidationException("Order amount does not meet minimum requirement: " + 
                    voucher.getDonHangToiThieu());
        }

        if (voucher.getSoLuongPhatHanh() != null &&
            voucher.getSoLuongDaDung() != null &&
            voucher.getSoLuongDaDung() >= voucher.getSoLuongPhatHanh()) {
            throw new ValidationException("Voucher usage limit reached");
        }

        if (voucher.getUsageLimitPerUser() != null) {
            List<CustomerVoucher> customerUsages = customerVoucherRepository.findByCustomerIdAndTrangThai(        
                    customerId, TrangThaiCustomerVoucher.DA_DUNG
            );
            long usageCount = customerUsages.stream()
                    .filter(cv -> cv.getVoucherId().equals(voucher.getId()))
                    .count();

            if (usageCount >= voucher.getUsageLimitPerUser()) {
                throw new ValidationException("Customer usage limit reached for this voucher");
            }
        }

        findAvailableCustomerVoucher(customerId, voucher.getId());
    }

    private CustomerVoucher findAvailableCustomerVoucher(String customerId, Long voucherId) {
        LocalDate today = LocalDate.now();
        return customerVoucherRepository.findByCustomerIdAndTrangThaiIn(
                customerId,
                List.of(TrangThaiCustomerVoucher.CHUA_DUNG)
        )
        .stream()
        .filter(cv -> cv.getVoucherId().equals(voucherId))
        .filter(cv -> cv.getExpiredAt() == null || !cv.getExpiredAt().toLocalDate().isBefore(today))
        .findFirst()
        .orElseThrow(() -> new ValidationException("Customer does not have this voucher available"));
    }

    private Double calculateDiscount(Voucher voucher, Double bookingAmount) {
        if (bookingAmount == null || bookingAmount <= 0) {
            return 0.0;
        }

        Double discountValue;
        if (voucher.getLoaiGiamGia() == LoaiGiamGia.PHAN_TRAM) {
            discountValue = bookingAmount * ((voucher.getMucGiam() != null ? voucher.getMucGiam() : 0.0) / 100.0);
        } else {
            discountValue = voucher.getMucGiam() != null ? voucher.getMucGiam() : 0.0;
        }

        if (voucher.getGiaTriGiamToiDa() != null) {
            discountValue = Math.min(discountValue, voucher.getGiaTriGiamToiDa());
        }

        return Math.min(discountValue, bookingAmount);
    }

    private BigDecimal getDiscountPercentage(Voucher voucher) {
        if (voucher.getLoaiGiamGia() == LoaiGiamGia.PHAN_TRAM) {
            return BigDecimal.valueOf(voucher.getMucGiam() != null ? voucher.getMucGiam() : 0.0);
        }
        return BigDecimal.ZERO;
    }

    private void ensureBookingIsPayable(DonDatCho booking) {
        if (booking.getTrangThai() != TrangThaiDon.CHO_THANH_TOAN) {
            throw new ValidationException("Booking is not payable");
        }
    }

    private Double normalizeMoney(Double value) {
        return value == null ? 0.0 : value;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class VoucherCheckoutResponse {
        private String bookingId;
        private Long voucherId;
        private String maVoucher;
        private BigDecimal originalAmount;
        private BigDecimal discountAmount;
        private BigDecimal finalAmount;
        private Boolean isApplied;
        private String message;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class VoucherPreviewResponse {
        private Long voucherId;
        private String maVoucher;
        private BigDecimal discountAmount;
        private BigDecimal discountPercentage;
        private BigDecimal estimatedFinalAmount;
        private Boolean isEligible;
        private String ineligibilityReason;
    }
}
