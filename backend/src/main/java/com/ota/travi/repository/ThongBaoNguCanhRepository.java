package com.ota.travi.repository;

import com.ota.travi.entity.ThongBaoNguCanh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ThongBaoNguCanhRepository extends JpaRepository<ThongBaoNguCanh, String> {
    List<ThongBaoNguCanh> findTop10ByThoiGianHieuLucAfterOrderByThoiGianHieuLucAsc(LocalDateTime now);
}
