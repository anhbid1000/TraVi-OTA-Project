package com.ota.travi.repository;

import com.ota.travi.entity.CustomerVoucher;
import com.ota.travi.enums.TrangThaiCustomerVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomerVoucherRepository extends JpaRepository<CustomerVoucher, Long> {
    List<CustomerVoucher> findByKhachHang_IdAndTrangThaiOrderByIssuedAtDesc(Long customerId, String status);

    List<CustomerVoucher> findByKhachHang_IdAndTrangThaiIn(Long customerId, List<String> statuses);

    @Query(value = """
            SELECT * FROM customer_voucher
            WHERE trang_thai = 'RESERVED'
            AND reserve_expires_at IS NOT NULL
            AND reserve_expires_at < :instant
            """, nativeQuery = true)
    List<CustomerVoucher> findReservedVouchersExpiredBefore(@Param("instant") Instant instant);

    List<CustomerVoucher> findByVoucherId(Long voucherId);

    List<CustomerVoucher> findByCustomerIdAndTrangThai(String customerId, TrangThaiCustomerVoucher trangThai);

    @Query("""
            SELECT cv FROM CustomerVoucher cv
            WHERE cv.customerId = :customerId
            AND cv.trangThai IN :statuses
            ORDER BY cv.issuedAt DESC
            """)
    List<CustomerVoucher> findByCustomerIdAndTrangThaiIn(
            @Param("customerId") String customerId,
            @Param("statuses") List<TrangThaiCustomerVoucher> statuses
    );

    @Query("""
            SELECT cv FROM CustomerVoucher cv
            WHERE cv.customerId = :customerId
            AND cv.trangThai = :trangThai
            """)
    List<CustomerVoucher> findByCustomerIdAndStatus(
            @Param("customerId") String customerId,
            @Param("trangThai") TrangThaiCustomerVoucher trangThai
    );
}
