package com.ota.travi.repository;

import com.ota.travi.entity.SuKienHanhVi;
import com.ota.travi.enums.HanhDongSuKien;
import com.ota.travi.enums.LoaiDoiTuongHanhVi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SuKienHanhViRepository extends JpaRepository<SuKienHanhVi, Long> {
    // Lấy tất cả hành vi của user
    List<SuKienHanhVi> findByUser_IdOrderByThoiGianDesc(String userId);

    // Lấy hành vi của user với một loại đối tượng cụ thể
    List<SuKienHanhVi> findByUser_IdAndLoaiDoiTuongOrderByThoiGianDesc(
            String userId,
            LoaiDoiTuongHanhVi loaiDoiTuong
    );

    // Lấy hành vi của user với một đối tượng cụ thể
    List<SuKienHanhVi> findByUser_IdAndDoiTuanIdAndLoaiDoiTuongOrderByThoiGianDesc(
            String userId,
            Long doiTuanId,
            LoaiDoiTuongHanhVi loaiDoiTuong
    );

    // Lấy hành vi trong khoảng thời gian
    List<SuKienHanhVi> findByUser_IdAndThoiGianBetweenOrderByThoiGianDesc(
            String userId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    // Đếm số lần user tương tác với một đối tượng
    @Query("SELECT COUNT(s) FROM SuKienHanhVi s WHERE s.user.id = :userId AND s.doiTuanId = :doiTuanId AND s.loaiDoiTuong = :loaiDoiTuong")
    Long countUserInteractionsWithEntity(
            @Param("userId") String userId,
            @Param("doiTuanId") Long doiTuanId,
            @Param("loaiDoiTuong") LoaiDoiTuongHanhVi loaiDoiTuong
    );

    // Lấy top entities được xem nhiều nhất
    @Query(value = "SELECT doi_tuan_id, COUNT(*) as so_luot FROM su_kien_hanh_vi " +
            "WHERE loai_doi_tuong = :loaiDoiTuong AND thoi_gian >= :fromTime " +
            "GROUP BY doi_tuan_id ORDER BY so_luot DESC LIMIT :limit",
            nativeQuery = true)
    List<Object[]> findTopEntitiesByViewCount(
            @Param("loaiDoiTuong") String loaiDoiTuong,
            @Param("fromTime") LocalDateTime fromTime,
            @Param("limit") int limit
    );

    // Lấy hành vi theo loại hành động
    List<SuKienHanhVi> findByUser_IdAndHanhDongOrderByThoiGianDesc(
            String userId,
            HanhDongSuKien hanhDong
    );
}
