package com.ota.travi.repository;

import com.ota.travi.entity.LichSuKiemDuyetHoSo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichSuKiemDuyetHoSoRepository extends JpaRepository<LichSuKiemDuyetHoSo, String> {
    List<LichSuKiemDuyetHoSo> findByHoSoKinhDoanh_IdHoSoOrderByCreatedAtDesc(String hoSoId);
}

