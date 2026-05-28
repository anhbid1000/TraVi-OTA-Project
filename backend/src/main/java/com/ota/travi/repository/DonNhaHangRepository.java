package com.ota.travi.repository;

import com.ota.travi.entity.DonNhaHang;
import com.ota.travi.enums.TrangThaiDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DonNhaHangRepository extends JpaRepository<DonNhaHang, String> {

    @Query("""
            SELECT COALESCE(SUM(dn.soNguoi), 0)
            FROM DonNhaHang dn
            WHERE dn.hoSoKinhDoanh.idHoSo = :businessProfileId
              AND dn.deleted = false
              AND dn.trangThaiDon IN :activeStatuses
              AND dn.ngayGioBatDau < :requestedEnd
              AND dn.ngayGioKetThuc > :requestedStart
            """)
    Integer sumBookedGuestsByBusinessProfileAndTimeSlot(
            @Param("businessProfileId") String businessProfileId,
            @Param("requestedStart") LocalDateTime requestedStart,
            @Param("requestedEnd") LocalDateTime requestedEnd,
            @Param("activeStatuses") List<TrangThaiDon> activeStatuses
    );

    @Query("""
            SELECT COUNT(link) > 0
            FROM DonNhaHangBan link
            WHERE link.ban.id = :tableId
              AND link.donNhaHang.deleted = false
              AND link.donNhaHang.trangThaiDon IN :activeStatuses
              AND link.donNhaHang.ngayGioBatDau < :requestedEnd
              AND link.donNhaHang.ngayGioKetThuc > :requestedStart
            """)
    boolean existsOverlappingReservationByTableAndTimeSlot(
            @Param("tableId") String tableId,
            @Param("requestedStart") LocalDateTime requestedStart,
            @Param("requestedEnd") LocalDateTime requestedEnd,
            @Param("activeStatuses") List<TrangThaiDon> activeStatuses
    );

    @Query("""
            SELECT COUNT(DISTINCT dn.id)
            FROM DonNhaHang dn
            JOIN dn.banDaGan link
            WHERE link.ban.nhaHang.idTaiSan = :restaurantId
              AND dn.deleted = false
              AND dn.trangThaiDon IN :activeStatuses
              AND dn.ngayTao >= :fromDateTime
            """)
    Integer countRecentReservationsByRestaurant(
            @Param("restaurantId") String restaurantId,
            @Param("fromDateTime") LocalDateTime fromDateTime,
            @Param("activeStatuses") List<TrangThaiDon> activeStatuses
    );
}

