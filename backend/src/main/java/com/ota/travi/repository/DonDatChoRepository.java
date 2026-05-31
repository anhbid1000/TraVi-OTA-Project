package com.ota.travi.repository;

import com.ota.travi.entity.DonDatCho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonDatChoRepository extends JpaRepository<DonDatCho, String> {
    boolean existsByMaDon(String maDon);
    Optional<DonDatCho> findByMaDonAndDeletedFalse(String maDon);
    List<DonDatCho> findByKhachHang_UsernameAndDeletedFalseOrderByNgayTaoDesc(String username);
}

