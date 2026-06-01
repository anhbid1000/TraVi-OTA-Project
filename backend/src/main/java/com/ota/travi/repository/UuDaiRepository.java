package com.ota.travi.repository;

import com.ota.travi.entity.UuDai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UuDaiRepository extends JpaRepository<UuDai, Long> {
    @Override
    Optional<UuDai> findById(Long id);

    List<UuDai> findByDeletedFalseAndTrangThaiUuDaiIn(List<String> statuses);

    List<UuDai> findByCreatedByUserIdAndDeletedFalse(String userId);

    List<UuDai> findByBusinessProfileIdAndDeletedFalse(Long businessProfileId);

    @Query(value = """
            SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END
            FROM uudai
            WHERE target_type = :targetType
            AND target_id = :targetId
            AND trang_thai_uudai IN :statuses
            AND ((ngay_bat_dau <= :endDate AND ngay_ket_thuc >= :startDate))
            """, nativeQuery = true)
    boolean existsOverlappingPromotion(
            @Param("targetType") String targetType,
            @Param("targetId") Long targetId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("statuses") List<String> statuses
    );

    @Modifying
    @Query("UPDATE UuDai u SET u.trangThaiUuDai = :newStatus WHERE u.ngayKetThuc <= :now AND u.trangThaiUuDai IN :oldStatuses AND u.deleted = false")
    void updateStatusForExpiredPromotions(
            @Param("newStatus") String newStatus,
            @Param("now") LocalDateTime now,
            @Param("oldStatuses") List<String> oldStatuses
    );
}
