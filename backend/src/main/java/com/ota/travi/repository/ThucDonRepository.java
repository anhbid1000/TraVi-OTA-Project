package com.ota.travi.repository;

import com.ota.travi.entity.ThucDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThucDonRepository extends JpaRepository<ThucDon, String> {
    List<ThucDon> findByNhaHang_IdTaiSan(String nhaHangId);

    List<ThucDon> findByNhaHang_IdTaiSanAndPhanLoai(String nhaHangId, String phanLoai);

    void deleteByNhaHang_IdTaiSan(String nhaHangId);
}
