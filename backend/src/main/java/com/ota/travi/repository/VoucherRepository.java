package com.ota.travi.repository;

import com.ota.travi.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Optional<Voucher> findByMaVoucher(String maVoucher);

    boolean existsByMaVoucherIgnoreCase(String maVoucher);

    List<Voucher> findByDeletedFalseOrderByCreatedAtDesc();

    @Query("""
            SELECT v FROM Voucher v
            WHERE v.deleted = false
              AND v.choPhepDoiBangDiem = true
              AND COALESCE(v.soLuongDaDung, 0) < COALESCE(v.soLuongPhatHanh, 0)
            ORDER BY v.createdAt DESC
            """)
    List<Voucher> findExchangeableVouchers();
}
