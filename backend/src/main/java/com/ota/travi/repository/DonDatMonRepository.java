package com.ota.travi.repository;

import com.ota.travi.entity.DonDatMon;
import com.ota.travi.enums.TrangThaiDonDatCho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface DonDatMonRepository extends JpaRepository<DonDatMon, String> {
    boolean existsByBan_IdAndThoiGianDatAfterAndDonDatCho_TrangThaiNotIn(
            String banId,
            LocalDateTime thoiGianDat,
            Collection<TrangThaiDonDatCho> ignoredStatuses
    );
}
