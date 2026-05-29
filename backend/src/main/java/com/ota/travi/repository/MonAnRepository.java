package com.ota.travi.repository;

import com.ota.travi.entity.MonAn;
import com.ota.travi.enums.TrangThaiMonAn;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonAnRepository extends JpaRepository<MonAn, String> {
    List<MonAn> findByThucDon_IdAndDeletedFalse(String thucDonId);

    List<MonAn> findByThucDon_NhaHang_IdTaiSanAndDeletedFalse(String nhaHangId);

    List<MonAn> findByTrangThaiAndDeletedFalse(TrangThaiMonAn trangThai);
}
