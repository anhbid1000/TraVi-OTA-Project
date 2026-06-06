package com.ota.travi.repository;

import com.ota.travi.entity.DatPhong;
import com.ota.travi.enums.TrangThaiDonDatCho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;

@Repository
public interface DatPhongRepository extends JpaRepository<DatPhong, String> {
    boolean existsByPhong_IdAndNgayCheckInAfterAndDonDatCho_TrangThaiNotIn(
            String phongId,
            LocalDate ngayCheckIn,
            Collection<TrangThaiDonDatCho> ignoredStatuses
    );
}
