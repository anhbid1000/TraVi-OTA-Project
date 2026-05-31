package com.ota.travi.repository;

import com.ota.travi.entity.ChiTietDonDatMon;
import com.ota.travi.enums.TrangThaiDon;
import com.ota.travi.enums.TrangThaiDonDatCho;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface ChiTietDonDatMonRepository extends JpaRepository<ChiTietDonDatMon, String> {
    boolean existsByMonAn_IdAndDonDatMon_ThoiGianDatAfterAndDonDatMon_DonDatCho_TrangThaiNotIn(
            String monAnId,
            LocalDateTime thoiGianDat,
            Collection<TrangThaiDonDatCho> ignoredStatuses
    );

    interface TopDishAggregate {
        String getMonAnId();

        String getTenMon();

        String getAnhMon();

        Long getTongSoLuong();

        Double getGiaTrungBinh();
    }

    @Query("""
            SELECT
                ct.monAn.id AS monAnId,
                ct.monAn.tenMon AS tenMon,
                ct.monAn.duongDanUrl AS anhMon,
                COALESCE(SUM(ct.soLuong), 0) AS tongSoLuong,
                COALESCE(AVG(ct.giaTien), 0) AS giaTrungBinh
            FROM ChiTietDonDatMon ct
            WHERE ct.donDatMon.nhaHang.idTaiSan = :restaurantId
              AND ct.donDatMon.donDatCho.deleted = false
              AND ct.donDatMon.donDatCho.trangThai IN :statuses
              AND ct.donDatMon.donDatCho.ngayTao >= :fromDateTime
              AND ct.donDatMon.donDatCho.ngayTao < :toDateTime
            GROUP BY ct.monAn.id, ct.monAn.tenMon, ct.monAn.duongDanUrl
            ORDER BY COALESCE(SUM(ct.soLuong), 0) DESC
            """)
    List<TopDishAggregate> findTopDishesForDashboard(
            @Param("restaurantId") String restaurantId,
            @Param("statuses") List<TrangThaiDon> statuses,
            @Param("fromDateTime") LocalDateTime fromDateTime,
            @Param("toDateTime") LocalDateTime toDateTime,
            Pageable pageable
    );
}
