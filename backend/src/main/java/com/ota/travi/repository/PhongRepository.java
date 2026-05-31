package com.ota.travi.repository;

import com.ota.travi.entity.Phong;
import com.ota.travi.enums.TrangThaiPhong;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhongRepository extends JpaRepository<Phong, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Phong> findById(String id);

    List<Phong> findByKhachSan_IdTaiSanAndDeletedFalse(String khachSanId);

    List<Phong> findByKhachSan_IdTaiSanAndTrangThaiAndDeletedFalse(String khachSanId, TrangThaiPhong trangThai);

    boolean existsByKhachSan_IdTaiSanAndSoPhongAndDeletedFalse(String khachSanId, String soPhong);
    List<Phong> findByKhachSan_IdTaiSan(String khachSanId);

    List<Phong> findByKhachSan_IdTaiSanAndTrangThai(String khachSanId, TrangThaiPhong trangThai);

    boolean existsByKhachSan_IdTaiSanAndSoPhong(String khachSanId, String soPhong);

    void deleteByKhachSan_IdTaiSan(String khachSanId);
}
