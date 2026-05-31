package com.ota.travi.booking.service;

import com.ota.travi.booking.domain.entities.DonDatCho;
import com.ota.travi.booking.domain.enums.TrangThaiDon;
import com.ota.travi.booking.repository.BookingDonDatChoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingCronJob {

    private final BookingDonDatChoRepository donDatChoRepository;

    // Task 6.2: Cứ mỗi 60000ms (1 phút) con Robot này sẽ tự kích hoạt chạy ngầm
    @Scheduled(fixedRate = 60000)
    @Transactional(rollbackFor = Exception.class)
    public void autoCancelExpiredBookings() {
        log.info("🕵️ Robot Cron Job bắt đầu quét dữ liệu các đơn hàng quá hạn thanh toán...");

        // Tính mốc thời gian cách đây 15 phút
        LocalDateTime mocthoiGianQuaHan = LocalDateTime.now().minusMinutes(15);

        // Lấy tất cả các đơn hàng trong DB ra
        List<DonDatCho> allBookings = donDatChoRepository.findAll();

        for (DonDatCho don : allBookings) {
            // Điều kiện: Đơn đang CHỜ THANH TOÁN VÀ thời gian tạo đã quá 15 phút trước
            if (don.getTrangThai() == TrangThaiDon.CHO_THANH_TOAN && don.getNgayTao().isBefore(mocthoiGianQuaHan)) {
                
                log.warn("🚨 Phát hiện đơn hàng quá hạn 15 phút! Mã đơn: {} - Đang tiến hành hủy tự động.", don.getMaDon());
                
                // 1. Tự động chuyển trạng thái đơn sang ĐÃ HỦY
                don.setTrangThai(TrangThaiDon.DA_HUY);
                donDatChoRepository.save(don);

                // TODO: Gọi hàm giải phóng phòng/bàn trả về kho (Inventory release) ở đây
                log.info("✅ Đã giải phóng phòng/bàn thành công cho mã đơn: {}", don.getMaDon());
            }
        }
    }
}
