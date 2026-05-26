package com.ota.travi.repository;

import com.ota.travi.entity.DonKhachSanChiTiet;
import com.ota.travi.enums.TrangThaiDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DonKhachSanChiTietRepository extends JpaRepository<DonKhachSanChiTiet, String> {

    @Query("""
            SELECT COALESCE(SUM(ct.soLuong), 0)
            FROM DonKhachSanChiTiet ct
            WHERE ct.phong.id = :roomId
              AND ct.donKhachSan.deleted = false
              AND ct.donKhachSan.trangThaiDon IN :activeStatuses
              AND ct.donKhachSan.ngayCheckIn < :requestedCheckOut
              AND ct.donKhachSan.ngayCheckOut > :requestedCheckIn
            """)
    Integer sumBookedQuantityByRoomAndDateRange(
            @Param("roomId") String roomId,
            @Param("requestedCheckIn") LocalDate requestedCheckIn,
            @Param("requestedCheckOut") LocalDate requestedCheckOut,
            @Param("activeStatuses") List<TrangThaiDon> activeStatuses
    );
}

