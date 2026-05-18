package com.ota.travi.repository;

import com.ota.travi.entity.AnhKhachSan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnhKhachSanRepository extends JpaRepository<AnhKhachSan, String> {
    List<AnhKhachSan> findByKhachSan_IdTaiSan(String khachSanId);

    Optional<AnhKhachSan> findFirstByKhachSan_IdTaiSanAndLaAnhDaiDienTrue(String khachSanId);

    void deleteByKhachSan_IdTaiSan(String khachSanId);
}
