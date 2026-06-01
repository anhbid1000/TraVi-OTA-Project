package com.ota.travi.repository;

import com.ota.travi.entity.KhuyenMaiTrucTiep;
import com.ota.travi.entity.UuDai;
import com.ota.travi.enums.TargetType;
import com.ota.travi.enums.TrangThaiUuDai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UuDaiRepository extends JpaRepository<UuDai, Long> {
    @Override
    Optional<UuDai> findById(Long id);

    List<UuDai> findByDeletedFalseAndTrangThaiUuDaiIn(List<TrangThaiUuDai> statuses);

    List<UuDai> findByCreatedByUserIdAndDeletedFalse(String userId);

    List<UuDai> findByBusinessProfileIdAndDeletedFalse(String businessProfileId);

    @Query("""
            SELECT CASE WHEN COUNT(km) > 0 THEN true ELSE false END
            FROM KhuyenMaiTrucTiep km
            WHERE km.targetType = :targetType
              AND km.targetId = :targetId
              AND km.trangThaiUuDai IN :statuses
              AND km.deleted = false
              AND km.ngayBatDau <= :endDate
              AND km.ngayKetThuc >= :startDate
            """)
    boolean existsOverlappingPromotion(
            @Param("targetType") TargetType targetType,
            @Param("targetId") String targetId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("statuses") List<TrangThaiUuDai> statuses
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE UuDai u
            SET u.trangThaiUuDai = :newStatus
            WHERE u.ngayKetThuc < :today
              AND u.trangThaiUuDai IN :oldStatuses
              AND u.deleted = false
            """)
    int updateStatusForExpiredPromotions(
            @Param("newStatus") TrangThaiUuDai newStatus,
            @Param("today") LocalDate today,
            @Param("oldStatuses") List<TrangThaiUuDai> oldStatuses
    );
}
