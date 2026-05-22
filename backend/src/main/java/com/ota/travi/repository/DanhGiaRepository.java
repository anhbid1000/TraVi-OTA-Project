package com.ota.travi.repository;

import com.ota.travi.entity.DanhGia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DanhGiaRepository extends JpaRepository<DanhGia, String> {
    @Query("SELECT d FROM DanhGia d WHERE (:soSao IS NULL OR d.soSao = :soSao)")
    Page<DanhGia> findByFilter(@Param("soSao") Integer soSao, Pageable pageable);
}
