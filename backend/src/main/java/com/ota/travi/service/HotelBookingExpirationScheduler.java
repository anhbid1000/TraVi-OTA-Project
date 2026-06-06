package com.ota.travi.service;

import com.ota.travi.entity.DonKhachSan;
import com.ota.travi.enums.TrangThaiDon;
import com.ota.travi.repository.DonKhachSanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HotelBookingExpirationScheduler {

    @Autowired
    private DonKhachSanRepository donKhachSanRepository;

    @Autowired
    private HotelBookingPaymentHoldService hotelBookingPaymentHoldService;

    @Autowired
    private PaymentCallbackService paymentCallbackService;

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void cancelExpiredPendingHotelBookings() {
        List<DonKhachSan> expiredBookings = donKhachSanRepository.findByTrangThaiAndPaymentExpiredAtBeforeAndDeletedFalse(
                TrangThaiDon.CHO_THANH_TOAN,
                LocalDateTime.now()
        );

        for (DonKhachSan booking : expiredBookings) {
            try {
                paymentCallbackService.onPaymentFail(booking.getId(), booking.getKhachHang().getId());
            } catch (RuntimeException ignored) {
                // Keep cancellation flow resilient even when voucher release hits inconsistent data.
            }
            booking.setTrangThai(TrangThaiDon.DA_HUY);
            booking.setCancelledAt(LocalDateTime.now());
            booking.setCancelReason("Quá thời gian thanh toán 20 phút");
            hotelBookingPaymentHoldService.removePending(booking.getId());
        }

        donKhachSanRepository.saveAll(expiredBookings);
    }
}
