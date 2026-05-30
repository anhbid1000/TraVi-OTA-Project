package com.ota.travi.repository;

import com.ota.travi.entity.DonDatCho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonDatChoRepository extends JpaRepository<DonDatCho, String> {
    List<DonDatCho> findByKhachHang_IdAndDeletedFalseOrderByNgayTaoDesc(String khachHangId);
}
