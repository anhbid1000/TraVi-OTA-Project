package com.ota.travi.repository;

import com.ota.travi.entity.DonKhachSan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonKhachSanRepository extends JpaRepository<DonKhachSan, String> {
}
