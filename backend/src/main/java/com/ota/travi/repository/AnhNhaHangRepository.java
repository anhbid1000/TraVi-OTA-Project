package com.ota.travi.repository;

import com.ota.travi.entity.AnhNhaHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnhNhaHangRepository extends JpaRepository<AnhNhaHang, String> {
    List<AnhNhaHang> findByNhaHang_IdTaiSan(String nhaHangId);

    Optional<AnhNhaHang> findFirstByNhaHang_IdTaiSanAndLaAnhDaiDienTrue(String nhaHangId);

    void deleteByNhaHang_IdTaiSan(String nhaHangId);
}
