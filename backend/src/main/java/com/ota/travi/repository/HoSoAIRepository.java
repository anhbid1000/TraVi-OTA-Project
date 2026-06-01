package com.ota.travi.repository;

import com.ota.travi.entity.HoSoAI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HoSoAIRepository extends JpaRepository<HoSoAI, String> {
    Optional<HoSoAI> findByKhachHang_Id(String khachHangId);
}
