package com.ota.travi.repository;

import com.ota.travi.entity.LichSuDiem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichSuDiemRepository extends JpaRepository<LichSuDiem, Long> {
    List<LichSuDiem> findByKhachHang_IdOrderByCreatedAtDesc(Long customerId);

    List<LichSuDiem> findByLoaiGiaoDichDiem(String loaiGiaoDichDiem);
}
