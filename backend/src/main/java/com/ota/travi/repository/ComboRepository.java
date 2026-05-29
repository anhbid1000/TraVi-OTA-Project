package com.ota.travi.repository;

import com.ota.travi.entity.Combo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComboRepository extends JpaRepository<Combo, String> {
    List<Combo> findByThucDon_Id(String thucDonId);

    List<Combo> findByThucDon_NhaHang_IdTaiSan(String nhaHangId);

    List<Combo> findByThucDon_NhaHang_IdTaiSanAndTrangThaiNot(String nhaHangId, Integer trangThai);

    List<Combo> findByTrangThai(Integer trangThai);
}
