package com.traviota.booking.service;

import com.traviota.booking.domain.entities.DonDatCho;
import com.traviota.booking.domain.entities.GiaoDichThanhToan;
import com.traviota.booking.domain.enums.TrangThaiDon;
import com.traviota.booking.repository.DonDatChoRepository;
import com.traviota.booking.repository.GiaoDichThanhToanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final DonDatChoRepository donDatChoRepository;
    private final GiaoDichThanhToanRepository giaoDichThanhToanRepository;

    // Task 2.3a: Tạo URL giả lập để Frontend chuyển hướng khách đi trả tiền
    public String createCheckoutUrl(Long bookingId) {
        DonDatCho don = donDatChoRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        return "https://mock-payment.com/checkout?orderId=" + don.getId() + "&amount=" + don.getTongTien();
    }

    // Task 2.3b & 6.4: Nhận kết quả trả về từ Ngân hàng (Webhook) có chống lặp
    @Transactional(rollbackFor = Exception.class)
    public String handleWebhook(Long bookingId, String maGiaoDich, String phuongThuc, BigDecimal soTien) {
        
        // Chống lặp (Idempotency): Nếu ngân hàng gửi trùng tin nhắn thành công, mình chặn lại luôn
        if (giaoDichThanhToanRepository.existsByMaGiaoDichNganHang(maGiaoDich)) {
            return "ALREADY_PROCESSED";
        }

        // Tìm đơn và dùng khóa Pessimistic Lock (Task 6.1) để giam đơn lại xử lý cho an toàn
        DonDatCho don = donDatChoRepository.findByIdWithLock(bookingId)
                .orElseThrow(() -> new RuntimeException("Không thấy đơn hàng"));

        if (don.getTrangThai() == TrangThaiDon.DA_THANH_TOAN) {
            return "SUCCESS";
        }

        // Đổi trạng thái đơn thành ĐÃ THANH TOÁN
        don.setTrangThai(TrangThaiDon.DA_THANH_TOAN);
        donDatChoRepository.save(don);

        // Lưu lịch sử giao dịch vào bảng vệ tinh để kế toán đối soát
        GiaoDichThanhToan giaoDich = new GiaoDichThanhToan();
        giaoDich.setDonDatCho(don);
        giaoDich.setMaGiaoDichNganHang(maGiaoDich);
        giaoDich.setPhuongThuc(phuongThuc);
        giaoDich.setSoTien(soTien);
        giaoDichThanhToanRepository.save(giaoDich);

        return "SUCCESS";
    }
}