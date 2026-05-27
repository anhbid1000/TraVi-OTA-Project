package com.ota.travi.repository;

import com.ota.travi.entity.KhachSan;
import com.ota.travi.enums.TrangThaiTaiSan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KhachSanRepository extends JpaRepository<KhachSan, String> {
    List<KhachSan> findByHoSoKinhDoanh_IdHoSo(String hoSoKinhDoanhId);

    List<KhachSan> findByTrangThai(TrangThaiTaiSan trangThai);

    Optional<KhachSan> findByIdTaiSanAndHoSoKinhDoanh_IdHoSo(String khachSanId, String hoSoKinhDoanhId);

    boolean existsByIdTaiSanAndHoSoKinhDoanh_IdHoSo(String khachSanId, String hoSoKinhDoanhId);
}
