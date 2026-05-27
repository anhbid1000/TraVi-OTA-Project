package com.ota.travi.repository;

import com.ota.travi.entity.Phong;
import com.ota.travi.enums.TrangThaiPhong;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhongRepository extends JpaRepository<Phong, String> {
    List<Phong> findByKhachSan_IdTaiSan(String khachSanId);

    List<Phong> findByKhachSan_IdTaiSanAndTrangThai(String khachSanId, TrangThaiPhong trangThai);

    boolean existsByKhachSan_IdTaiSanAndSoPhong(String khachSanId, String soPhong);

    boolean existsByIdAndDeletedFalseAndKhachSan_HoSoKinhDoanh_IdHoSo(String id, String hoSoKinhDoanhId);

    void deleteByKhachSan_IdTaiSan(String khachSanId);
}
