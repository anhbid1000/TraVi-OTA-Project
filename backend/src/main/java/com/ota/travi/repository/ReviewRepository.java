package com.ota.travi.repository;

import com.ota.travi.entity.Review;
import com.ota.travi.enums.TrangThaiDanhGia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, String> {
    boolean existsByKhachHang_IdAndBookingId(String khachHangId, String bookingId);
    boolean existsByKhachHang_IdAndReservationId(String khachHangId, String reservationId);

    Page<Review> findByHoSoKinhDoanh_IdHoSoAndTrangThai(String hoSoId, TrangThaiDanhGia trangThai, Pageable pageable);
    Page<Review> findByKhachHang_Id(String khachHangId, Pageable pageable);

    long countByHoSoKinhDoanh_IdHoSoAndTrangThai(String hoSoId, TrangThaiDanhGia trangThai);
}

