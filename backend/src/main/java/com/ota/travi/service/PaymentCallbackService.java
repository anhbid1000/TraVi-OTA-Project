package com.ota.travi.service;

import com.ota.travi.entity.CustomerVoucher;
import com.ota.travi.entity.DonDatCho;
import com.ota.travi.enums.TrangThaiDon;
import com.ota.travi.enums.TrangThaiCustomerVoucher;
import com.ota.travi.exception.ResourceNotFoundException;
import com.ota.travi.repository.CustomerVoucherRepository;
import com.ota.travi.repository.DonDatChoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentCallbackService {

    private final CustomerVoucherRepository customerVoucherRepository;
    private final DonDatChoRepository donDatChoRepository;
    private final VoucherReserveService voucherReserveService;
    private final CustomerLoyaltyService customerLoyaltyService;
    private final PromotionAnalyticsService promotionAnalyticsService;
    private final NotificationEventService notificationEventService;

    public void onPaymentSuccess(String bookingId, String customerId) {
        log.info("Processing payment success for booking {} by customer {}", bookingId, customerId);

        try {
            DonDatCho booking = donDatChoRepository.findById(bookingId)
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

            if (!booking.getKhachHang().getId().equals(customerId)) {
                throw new ResourceNotFoundException("Booking does not belong to customer");
            }

            if (booking.getVoucherId() != null) {
                Optional<CustomerVoucher> customerVoucherOpt = customerVoucherRepository.findById(booking.getVoucherId());
                if (customerVoucherOpt.isEmpty()) {
                    log.warn("Customer voucher not found with id: {}", booking.getVoucherId());
                } else {
                    CustomerVoucher customerVoucher = customerVoucherOpt.get();

                    if (customerVoucher.getTrangThai() == TrangThaiCustomerVoucher.RESERVED) {
                        voucherReserveService.consumeVoucher(booking.getVoucherId());
                        promotionAnalyticsService.trackBookingCompletion(
                                customerVoucher.getVoucherId(),
                                booking.getTongTienThanhToan(),
                                booking.getTienKhuyenMai()
                        );
                        notificationEventService.emitVoucherConsumed(
                                customerId,
                                customerVoucher.getVoucherId(),
                                parseLongOrNull(bookingId)
                        );
                        log.info("Successfully consumed voucher {} for booking {}", booking.getVoucherId(), bookingId);
                    } else if (customerVoucher.getTrangThai() != TrangThaiCustomerVoucher.DA_DUNG) {
                        log.warn(
                                "Voucher {} is not reserved. Current status: {}. Skipping consume.",
                                booking.getVoucherId(),
                                customerVoucher.getTrangThai()
                        );
                    }
                }

                booking.setVoucherId(null);
            }

            if (booking.getTrangThai() == TrangThaiDon.CHO_THANH_TOAN) {
                booking.setTrangThai(TrangThaiDon.DA_THANH_TOAN);
            }
            donDatChoRepository.save(booking);
            customerLoyaltyService.processBookingPaymentSuccess(booking);

        } catch (Exception e) {
            log.error("Error processing payment success callback for booking {}. Reason: {}", bookingId, e.getMessage(), e);
            throw e;
        }
    }

    public void onPaymentFail(String bookingId, String customerId) {
        log.info("Processing payment failure for booking {} by customer {}", bookingId, customerId);

        try {
            DonDatCho booking = donDatChoRepository.findById(bookingId)
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId)); 

            if (!booking.getKhachHang().getId().equals(customerId)) {
                throw new ResourceNotFoundException("Booking does not belong to customer");
            }

            if (booking.getVoucherId() != null) {
                Optional<CustomerVoucher> customerVoucherOpt = customerVoucherRepository.findById(booking.getVoucherId());
                if (customerVoucherOpt.isEmpty()) {
                    log.warn("Customer voucher not found with id: {}", booking.getVoucherId());
                } else {
                    CustomerVoucher customerVoucher = customerVoucherOpt.get();

                    if (customerVoucher.getTrangThai() == TrangThaiCustomerVoucher.RESERVED) {
                        voucherReserveService.releaseVoucher(booking.getVoucherId());
                        notificationEventService.emitVoucherReleased(customerId, customerVoucher.getVoucherId());
                        log.info("Successfully released voucher {} for failed booking {}", booking.getVoucherId(), bookingId);
                    } else {
                        log.debug("Voucher {} is not reserved. Current status: {}. Skipping release.",
                                booking.getVoucherId(), customerVoucher.getTrangThai());
                    }
                }
            }

            booking.setVoucherId(null);
            if (booking.getTrangThai() != TrangThaiDon.DA_THANH_TOAN) {
                booking.setTrangThai(TrangThaiDon.THANH_TOAN_THAT_BAI);
            }
            booking.setTienKhuyenMai(0.0);
            booking.setTongTienThanhToan(booking.getTongTienGoc());
            donDatChoRepository.save(booking);

        } catch (Exception e) {
            log.error("Error processing payment failure callback for booking {}. Reason: {}", bookingId, e.getMessage(), e);
            throw e;
        }
    }

    private Long parseLongOrNull(String bookingId) {
        try {
            return bookingId != null ? Long.valueOf(bookingId) : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
