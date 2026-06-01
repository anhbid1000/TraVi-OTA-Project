package com.ota.travi.service;

import com.ota.travi.dto.request.CancelBookingRequest;
import com.ota.travi.dto.response.CancelBookingResponse;
import com.ota.travi.entity.DonDatCho;
import com.ota.travi.entity.DonKhachSan;
import com.ota.travi.entity.DonNhaHang;
import com.ota.travi.enums.TrangThaiDon;
import com.ota.travi.repository.DonDatChoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CancelBookingService {

    @Autowired
    private DonDatChoRepository donDatChoRepository;

    @Transactional
    public CancelBookingResponse cancelBooking(String bookingId, String customerId, CancelBookingRequest request) {
        DonDatCho booking = donDatChoRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt chỗ"));

        // Verify ownership
        if (booking.getKhachHang() == null || !customerId.equals(booking.getKhachHang().getId())) {
            throw new RuntimeException("Bạn không có quyền hủy đơn đặt chỗ này");
        }

        // Check if already cancelled
        if (booking.getTrangThai() == TrangThaiDon.DA_HUY) {
            throw new RuntimeException("Đơn đặt chỗ đã bị hủy trước đó");
        }

        // Check if can be cancelled (only certain statuses allowed)
        if (booking.getTrangThai() == TrangThaiDon.DANG_PHUC_VU || 
            booking.getTrangThai() == TrangThaiDon.DA_HOAN_THANH) {
            throw new RuntimeException("Không thể hủy đơn đặt chỗ ở trạng thái này");
        }

        // Calculate refund based on booking type and timing
        double refundAmount = 0.0;
        String cancelReason = request.reason() != null ? request.reason() : "Khách hàng yêu cầu hủy";

        if (booking instanceof DonKhachSan hotelBooking) {
            refundAmount = calculateHotelRefund(hotelBooking);
        } else if (booking instanceof DonNhaHang restaurantBooking) {
            refundAmount = calculateRestaurantRefund(restaurantBooking);
        }

        // Update booking status
        booking.setTrangThai(TrangThaiDon.DA_HUY);
        booking.setCancelledAt(LocalDateTime.now());
        booking.setCancelReason(cancelReason);
        donDatChoRepository.save(booking);

        String message = buildCancelMessage(booking, refundAmount);

        return new CancelBookingResponse(
                true,
                message,
                refundAmount,
                booking.getId(),
                booking.getMaDon()
        );
    }

    /**
     * Hotel cancellation logic:
     * - Cancel before 24h: full refund
     * - Cancel within 24h: no refund (forfeit payment)
     */
    private double calculateHotelRefund(DonKhachSan hotelBooking) {
        LocalDateTime checkInDate = hotelBooking.getNgayCheckIn() != null 
                ? hotelBooking.getNgayCheckIn().atStartOfDay() 
                : null;

        if (checkInDate == null) {
            return 0.0;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cancelDeadline = checkInDate.minusHours(24);

        // If cancelling before 24h before check-in: full refund
        if (now.isBefore(cancelDeadline)) {
            return hotelBooking.getTongTienThanhToan() != null ? hotelBooking.getTongTienThanhToan() : 0.0;
        }

        // If cancelling within 24h: no refund
        return 0.0;
    }

    /**
     * Restaurant cancellation logic:
     * - Cancel before 24h: full refund of deposit
     * - Cancel within 24h: forfeit deposit (no refund)
     */
    private double calculateRestaurantRefund(DonNhaHang restaurantBooking) {
        LocalDateTime reservationTime = restaurantBooking.getNgayGioBatDau();

        if (reservationTime == null) {
            return 0.0;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cancelDeadline = reservationTime.minusHours(24);

        // If cancelling before 24h before reservation: full refund of deposit
        if (now.isBefore(cancelDeadline)) {
            return restaurantBooking.getTienCoc() != null ? restaurantBooking.getTienCoc() : 0.0;
        }

        // If cancelling within 24h: no refund (forfeit deposit)
        return 0.0;
    }

    private String buildCancelMessage(DonDatCho booking, double refundAmount) {
        String bookingType = booking instanceof DonKhachSan ? "khách sạn" : "nhà hàng";
        
        if (refundAmount > 0) {
            return String.format("Hủy đơn %s thành công. Sẽ hoàn tiền %.0f VNĐ trong 3-5 ngày làm việc.", 
                    bookingType, refundAmount);
        } else {
            if (booking instanceof DonKhachSan) {
                return "Hủy đơn khách sạn thành công. Vì hủy trong 24 giờ trước nhận phòng nên không được hoàn tiền.";
            } else {
                return "Hủy đơn nhà hàng thành công. Tiền cọc sẽ bị mất do hủy trong 24 giờ trước giờ đặt bàn.";
            }
        }
    }
}
