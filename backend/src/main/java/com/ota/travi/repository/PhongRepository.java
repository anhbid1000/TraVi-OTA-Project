package com.ota.travi.repository;

import com.ota.travi.entity.Phong;
import com.ota.travi.enums.TrangThaiPhong;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhongRepository extends JpaRepository<Phong, String> {
    List<Phong> findByKhachSan_IdTaiSanAndDeletedFalse(String khachSanId);

    List<Phong> findByKhachSan_IdTaiSanAndTrangThaiAndDeletedFalse(String khachSanId, TrangThaiPhong trangThai);

    boolean existsByKhachSan_IdTaiSanAndSoPhongAndDeletedFalse(String khachSanId, String soPhong);

    void deleteByKhachSan_IdTaiSan(String khachSanId);
}
