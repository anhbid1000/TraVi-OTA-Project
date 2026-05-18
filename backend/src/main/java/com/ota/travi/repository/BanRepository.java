package com.ota.travi.repository;

import com.ota.travi.entity.Ban;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BanRepository extends JpaRepository<Ban, String> {
    List<Ban> findByNhaHang_IdTaiSan(String nhaHangId);

    List<Ban> findByNhaHang_IdTaiSanAndTrangThai(String nhaHangId, Integer trangThai);

    void deleteByNhaHang_IdTaiSan(String nhaHangId);
}
