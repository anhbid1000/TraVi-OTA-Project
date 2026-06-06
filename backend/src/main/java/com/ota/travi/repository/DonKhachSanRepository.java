package com.ota.travi.repository;

import com.ota.travi.entity.DonKhachSan;
import com.ota.travi.enums.TrangThaiDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DonKhachSanRepository extends JpaRepository<DonKhachSan, String> {
    boolean existsByMaDon(String maDon);

    Optional<DonKhachSan> findByIdAndKhachHang_Username(String id, String username);

    Optional<DonKhachSan> findByMaDonAndDeletedFalse(String maDon);

    List<DonKhachSan> findByKhachHang_UsernameAndDeletedFalseOrderByNgayTaoDesc(String username);

    List<DonKhachSan> findByKhachHang_IdAndDeletedFalseOrderByNgayTaoDesc(String khachHangId);

    List<DonKhachSan> findByKhachHang_UsernameAndDeletedFalse(String username);

    List<DonKhachSan> findByTrangThaiAndPaymentExpiredAtBeforeAndDeletedFalse(
            TrangThaiDon trangThai,
            LocalDateTime dateTime
    );

    @Query("""
            SELECT DISTINCT d
            FROM DonKhachSan d
            LEFT JOIN FETCH d.chiTietDon ct
            LEFT JOIN FETCH ct.phong p
            WHERE d.hoSoKinhDoanh.idHoSo = :businessProfileId
              AND d.deleted = false
              AND d.trangThai IN :statuses
              AND d.ngayTao >= :fromDateTime
              AND d.ngayTao < :toDateTime
            ORDER BY d.ngayTao DESC
            """)
    List<DonKhachSan> findDashboardOrders(
            @Param("businessProfileId") String businessProfileId,
            @Param("statuses") List<TrangThaiDon> statuses,
            @Param("fromDateTime") LocalDateTime fromDateTime,
            @Param("toDateTime") LocalDateTime toDateTime
    );

    Page<DonKhachSan> findByHoSoKinhDoanh_IdHoSoAndDeletedFalseAndTrangThaiInOrderByNgayTaoDesc(
            String businessProfileId,
            List<TrangThaiDon> statuses,
            Pageable pageable
    );
}
