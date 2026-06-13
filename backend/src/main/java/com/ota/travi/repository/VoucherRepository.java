package com.ota.travi.repository;

import com.ota.travi.entity.Voucher;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Optional<Voucher> findByMaVoucher(String maVoucher);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Voucher v WHERE v.id = :id")
    Optional<Voucher> findByIdForUpdate(Long id);

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
