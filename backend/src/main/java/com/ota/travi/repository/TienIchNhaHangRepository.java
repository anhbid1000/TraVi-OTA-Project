package com.ota.travi.repository;

import com.ota.travi.entity.TienIchNhaHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TienIchNhaHangRepository extends JpaRepository<TienIchNhaHang, String> {
    List<TienIchNhaHang> findByNhaHang_IdTaiSan(String nhaHangId);

    List<TienIchNhaHang> findByNhaHang_IdTaiSanAndCoThuPhi(String nhaHangId, Boolean coThuPhi);

    void deleteByNhaHang_IdTaiSan(String nhaHangId);
}
