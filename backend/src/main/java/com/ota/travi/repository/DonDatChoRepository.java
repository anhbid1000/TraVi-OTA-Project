package com.ota.travi.repository;

import com.ota.travi.entity.DonDatCho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonDatChoRepository extends JpaRepository<DonDatCho, String>, JpaSpecificationExecutor<DonDatCho> {
    boolean existsByMaDon(String maDon);

    Optional<DonDatCho> findByMaDonAndDeletedFalse(String maDon);
    Optional<DonDatCho> findByIdAndDeletedFalse(String id);

    List<DonDatCho> findByKhachHang_IdAndDeletedFalseOrderByNgayTaoDesc(String khachHangId);

    List<DonDatCho> findByKhachHang_UsernameAndDeletedFalseOrderByNgayTaoDesc(String username);

    List<DonDatCho> findByKhachHang_UsernameAndDeletedFalse(String username);

    List<DonDatCho> findByHoSoKinhDoanh_DoiTac_IdAndDeletedFalse(String doiTacId);
}
