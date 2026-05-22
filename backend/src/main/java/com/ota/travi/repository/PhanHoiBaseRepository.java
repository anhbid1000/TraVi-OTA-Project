package com.ota.travi.repository;

import com.ota.travi.entity.DanhGia;
import com.ota.travi.entity.PhanHoiUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PhanHoiBaseRepository extends JpaRepository<PhanHoiUser, String> {
    @Query("SELECT d FROM DanhGia d WHERE d.trangThai = 'DA_DUYET'")
    List<DanhGia> findPublicReviews();
}
