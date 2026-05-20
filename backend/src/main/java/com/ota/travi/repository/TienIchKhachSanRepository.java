package com.ota.travi.repository;

import com.ota.travi.entity.TienIchKhachSan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TienIchKhachSanRepository extends JpaRepository<TienIchKhachSan, String> {
    Optional<TienIchKhachSan> findByTenTienIch(String tenTienIch);

    Optional<TienIchKhachSan> findByTenTienIchIgnoreCase(String tenTienIch);

    boolean existsByTenTienIch(String tenTienIch);
}
