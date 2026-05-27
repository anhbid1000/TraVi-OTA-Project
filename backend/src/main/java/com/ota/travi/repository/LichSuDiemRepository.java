package com.ota.travi.repository;

import com.ota.travi.entity.LichSuDiem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichSuDiemRepository extends JpaRepository<LichSuDiem, Long> {
    @Query("SELECT l FROM LichSuDiem l JOIN FETCH l.khachHang WHERE l.khachHang.id = :khachHangId ORDER BY l.createdAt DESC")
    List<LichSuDiem> findTop10ByKhachHang_IdOrderByCreatedAtDesc(String khachHangId);
}
