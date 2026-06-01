package com.ota.travi.repository;

import com.ota.travi.entity.CustomerVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomerVoucherRepository extends JpaRepository<CustomerVoucher, Long> {
    List<CustomerVoucher> findByCustomerIdOrderByIssuedAtDesc(String customerId);

    @Query(value = """
            SELECT * FROM customer_voucher
            WHERE reserve_expires_at IS NOT NULL
              AND reserve_expires_at < :expiresAt
            """, nativeQuery = true)
    List<CustomerVoucher> findReservedVouchersExpiredBefore(@Param("expiresAt") LocalDateTime expiresAt);

    List<CustomerVoucher> findByVoucherId(Long voucherId);
}
