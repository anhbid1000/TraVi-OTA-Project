package com.ota.travi.repository;

import com.ota.travi.entity.KhuyenMaiTrucTiep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KhuyenMaiTrucTiepRepository extends JpaRepository<KhuyenMaiTrucTiep, Long> {
}
