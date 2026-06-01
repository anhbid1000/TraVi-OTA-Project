package com.ota.travi.repository;

import com.ota.travi.entity.DanhMucSoThich;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DanhMucSoThichRepository extends JpaRepository<DanhMucSoThich, String> {
}
