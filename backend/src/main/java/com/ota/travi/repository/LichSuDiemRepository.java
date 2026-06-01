package com.ota.travi.repository;

import com.ota.travi.entity.LichSuDiem;
import com.ota.travi.enums.LoaiGiaoDichDiem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichSuDiemRepository extends JpaRepository<LichSuDiem, Long> {
    List<LichSuDiem> findByCustomerIdOrderByCreatedAtDesc(String customerId);

    List<LichSuDiem> findByLoaiGiaoDichDiem(String loaiGiaoDichDiem);

    boolean existsByCustomerIdAndLoaiGiaoDichDiemAndGhiChu(
            String customerId,
            LoaiGiaoDichDiem loaiGiaoDichDiem,
            String ghiChu
    );
}
