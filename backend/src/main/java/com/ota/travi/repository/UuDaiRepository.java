package com.ota.travi.repository;

import com.ota.travi.entity.UuDai;
import com.ota.travi.enums.TrangThaiUuDai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UuDaiRepository extends JpaRepository<UuDai, Long> {
    List<UuDai> findByDeletedFalseOrderByCreatedAtDesc();

    List<UuDai> findByCreatedByUserIdAndDeletedFalseOrderByCreatedAtDesc(String createdByUserId);

    @Modifying
    @Query("UPDATE UuDai u SET u.trangThaiUuDai = :newStatus " +
            "WHERE u.ngayKetThuc < :now " +
            "AND (u.trangThaiUuDai = :activeStatus OR u.trangThaiUuDai = :scheduledStatus)")
    int updateStatusForExpiredPromotions(
            @Param("newStatus") TrangThaiUuDai newStatus,
            @Param("now") LocalDateTime now,
            @Param("activeStatus") TrangThaiUuDai activeStatus,
            @Param("scheduledStatus") TrangThaiUuDai scheduledStatus
    );

    @Modifying
    @Query("UPDATE UuDai u SET u.trangThaiUuDai = :newStatus " +
            "WHERE u.ngayBatDau <= :now AND u.ngayKetThuc > :now " +
            "AND u.trangThaiUuDai = :scheduledStatus AND u.deleted = false")
    int updateStatusForActivePromotions(
            @Param("newStatus") TrangThaiUuDai newStatus,
            @Param("now") LocalDateTime now,
            @Param("scheduledStatus") TrangThaiUuDai scheduledStatus
    );

    @Query("SELECT COUNT(k) > 0 FROM KhuyenMaiTrucTiep k WHERE " +
            "k.targetType = :targetType AND k.targetId = :targetId " +
            "AND k.trangThai IN (:trangThais) " +
            "AND (:ngayBatDau <= k.ngayKetThuc AND :ngayKetThuc >= k.ngayBatDau)")
    boolean existsOverlappingPromotion(
            @Param("targetType") String targetType,
            @Param("targetId") Long targetId,
            @Param("ngayBatDau") LocalDateTime ngayBatDau,
            @Param("ngayKetThuc") LocalDateTime ngayKetThuc,
            @Param("trangThais") List<TrangThaiUuDai> trangThais
    );
}
