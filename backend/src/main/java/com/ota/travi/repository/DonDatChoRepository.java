package com.ota.travi.repository;

import com.ota.travi.entity.DonDatCho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface DonDatChoRepository extends JpaRepository<DonDatCho, String> {
    boolean existsByMaDon(String maDon);
     List<DonDatCho> findByKhachHang_IdAndDeletedFalseOrderByNgayTaoDesc(String khachHangId);
}

