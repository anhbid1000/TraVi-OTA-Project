package com.ota.travi.repository;

import com.ota.travi.entity.HoSoKinhDoanh;
import com.ota.travi.enums.LoaiDichVu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoSoKinhDoanhRepository extends JpaRepository<HoSoKinhDoanh, String> {
    boolean existsByMaSoThue(String maSoThue);

    Optional<HoSoKinhDoanh> findByMaSoThue(String maSoThue);
    List<HoSoKinhDoanh> findByLoaiDichVu(LoaiDichVu loaiDichVu);

    List<HoSoKinhDoanh> findByDoiTac_Id(String doiTacId);

    List<HoSoKinhDoanh> findByDoiTac_IdAndDeletedFalse(String doiTacId);
    boolean existsByDoiTac_IdAndMaSoThue(String doiTacId, String maSoThue);

    boolean existsByMaSoThueAndIdHoSoNot(String maSoThue, String idHoSo);
}
