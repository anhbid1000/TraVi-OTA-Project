package com.ota.travi.repository;

import com.ota.travi.entity.DonDatCho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DonDatChoRepository extends JpaRepository<DonDatCho, String>, JpaSpecificationExecutor<DonDatCho> {
    boolean existsByMaDon(String maDon);

    Optional<DonDatCho> findByMaDonAndDeletedFalse(String maDon);

    Optional<DonDatCho> findByIdAndDeletedFalse(String id);

    List<DonDatCho> findByKhachHang_IdAndDeletedFalseOrderByNgayTaoDesc(String khachHangId);

    List<DonDatCho> findByKhachHang_UsernameAndDeletedFalseOrderByNgayTaoDesc(String username);

    List<DonDatCho> findByKhachHang_UsernameAndDeletedFalse(String username);

    List<DonDatCho> findByHoSoKinhDoanh_DoiTac_IdAndDeletedFalse(String doiTacId);

    @Query(value = """
            SELECT
                cv.voucher_id AS campaignId,
                CAST(d.ngay_tao AS DATE) AS ngay,
                COUNT(*) AS usageCount,
                COUNT(*) AS bookingCount,
                COALESCE(SUM(d.tong_tien_thanh_toan), 0) AS generatedRevenue,
                COALESCE(SUM(d.tien_khuyen_mai), 0) AS discountCost
            FROM don_dat_cho d
            JOIN customer_voucher cv ON cv.id = d.voucher_id
            WHERE d.deleted = false
              AND d.voucher_id IS NOT NULL
              AND d.trang_thai IN (:statuses)
              AND d.ngay_tao >= :fromInclusive
              AND d.ngay_tao < :toExclusive
            GROUP BY cv.voucher_id, CAST(d.ngay_tao AS DATE)
            """, nativeQuery = true)
    List<PromotionDailyAggregate> aggregatePromotionAnalytics(
            @Param("statuses") List<String> statuses,
            @Param("fromInclusive") LocalDateTime fromInclusive,
            @Param("toExclusive") LocalDateTime toExclusive
    );

    interface PromotionDailyAggregate {
        Long getCampaignId();

        LocalDate getNgay();

        Long getUsageCount();

        Long getBookingCount();

        Double getGeneratedRevenue();

        Double getDiscountCost();
    }
}
