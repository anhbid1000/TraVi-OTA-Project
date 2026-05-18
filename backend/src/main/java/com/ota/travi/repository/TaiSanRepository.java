package com.ota.travi.repository;

import com.ota.travi.entity.TaiSan;
import com.ota.travi.enums.TrangThaiTaiSan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaiSanRepository extends JpaRepository<TaiSan, String> {
    List<TaiSan> findByHoSoKinhDoanh_IdHoSo(String hoSoKinhDoanhId);

    List<TaiSan> findByTrangThai(TrangThaiTaiSan trangThai);

    List<TaiSan> findByHoSoKinhDoanh_DoiTac_Id(String doiTacId);
}
