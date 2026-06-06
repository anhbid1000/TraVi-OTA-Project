package com.ota.travi.repository;

import com.ota.travi.entity.DonDatCho;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonDatChoRepository extends JpaRepository<DonDatCho, String> {
    boolean existsByMaDon(String maDon);
    Optional<DonDatCho> findByMaDonAndDeletedFalse(String maDon);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<DonDatCho> findByIdAndDeletedFalse(String id);
    List<DonDatCho> findByKhachHang_IdAndDeletedFalseOrderByNgayTaoDesc(String khachHangId);
    List<DonDatCho> findByKhachHang_UsernameAndDeletedFalseOrderByNgayTaoDesc(String username);
}

