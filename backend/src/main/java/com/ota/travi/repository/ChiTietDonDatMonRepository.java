package com.ota.travi.repository;

import com.ota.travi.entity.ChiTietDonDatMon;
import com.ota.travi.enums.TrangThaiDonDatCho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface ChiTietDonDatMonRepository extends JpaRepository<ChiTietDonDatMon, String> {
    boolean existsByMonAn_IdAndDonDatMon_ThoiGianDatAfterAndDonDatMon_DonDatCho_TrangThaiNotIn(
            String monAnId,
            LocalDateTime thoiGianDat,
            Collection<TrangThaiDonDatCho> ignoredStatuses
    );
}
