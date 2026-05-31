package com.ota.travi.repository;

import com.ota.travi.entity.KhuyenMaiTrucTiep;
import com.ota.travi.enums.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface KhuyenMaiTrucTiepRepository extends JpaRepository<KhuyenMaiTrucTiep, Long> {

    @Query("""
        SELECT COUNT(k) > 0 FROM KhuyenMaiTrucTiep k
        WHERE k.targetId = :targetId AND k.targetType = :targetType
          AND k.deleted = false
          AND k.trangThaiUuDai IN (com.ota.travi.enums.TrangThaiUuDai.DANG_CO_HIEU_LUC, com.ota.travi.enums.TrangThaiUuDai.DA_LEN_LICH, com.ota.travi.enums.TrangThaiUuDai.TAM_DUNG)
          AND k.ngayBatDau < :ngayKetThuc AND k.ngayKetThuc > :ngayBatDau
    """)
    boolean existsOverlappingPromotion(
            @Param("targetType") TargetType targetType,
            @Param("targetId") String targetId,
            @Param("ngayBatDau") LocalDateTime ngayBatDau,
            @Param("ngayKetThuc") LocalDateTime ngayKetThuc
    );
}
