package com.ota.travi.repository;

import com.ota.travi.entity.ChinhSach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChinhSachRepository extends JpaRepository<ChinhSach, String> {
    Optional<ChinhSach> findByHoSoKinhDoanh_IdHoSo(String hoSoKinhDoanhId);

    boolean existsByHoSoKinhDoanh_IdHoSo(String hoSoKinhDoanhId);
}
