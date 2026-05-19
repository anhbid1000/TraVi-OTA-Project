package com.traviota.booking.repository;

import com.traviota.booking.domain.entities.GiaoDichThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiaoDichThanhToanRepository extends JpaRepository<GiaoDichThanhToan, Long> {
    
    // Task 6.4: Kiểm tra xem mã giao dịch từ ngân hàng đã tồn tại chưa để chống lặp
    boolean existsByMaGiaoDichNganHang(String maGiaoDichNganHang);
}