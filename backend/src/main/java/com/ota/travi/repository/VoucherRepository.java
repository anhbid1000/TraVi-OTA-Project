package com.ota.travi.repository;

import com.ota.travi.entity.Voucher;
import com.ota.travi.enums.TrangThaiUuDai;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    boolean existsByMaVoucherIgnoreCase(String maVoucher);

    @Query("""
            select v
            from Voucher v
            where v.choPhepDoiBangDiem = true
              and v.trangThaiUuDai = :trangThai
              and v.deleted = false
              and coalesce(v.soLuongDaDung, 0) < v.soLuongPhatHanh
            order by v.createdAt desc
            """)
    List<Voucher> findExchangeableVouchers(@Param("trangThai") TrangThaiUuDai trangThai);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select v from Voucher v where v.id = :id")
    Optional<Voucher> findByIdForExchange(@Param("id") Long id);
    Optional<Voucher> findByMaVoucher(String maVoucher);
}
