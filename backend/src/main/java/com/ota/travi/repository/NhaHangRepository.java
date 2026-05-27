package com.ota.travi.repository;

import com.ota.travi.entity.NhaHang;
import com.ota.travi.enums.TrangThaiTaiSan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NhaHangRepository extends JpaRepository<NhaHang, String> {
    List<NhaHang> findByHoSoKinhDoanh_IdHoSo(String hoSoKinhDoanhId);

    List<NhaHang> findByTrangThai(TrangThaiTaiSan trangThai);

    Optional<NhaHang> findByIdTaiSanAndHoSoKinhDoanh_IdHoSo(String nhaHangId, String hoSoKinhDoanhId);

    boolean existsByIdTaiSanAndHoSoKinhDoanh_IdHoSo(String nhaHangId, String hoSoKinhDoanhId);
}
