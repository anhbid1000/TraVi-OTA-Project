package com.ota.travi.repository;

import com.ota.travi.entity.Voucher;
import com.ota.travi.enums.TrangThaiUuDai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Optional<Voucher> findByMaVoucher(String maVoucher);

    boolean existsByMaVoucherIgnoreCase(String maVoucher);

    @Query(value = """
            SELECT * FROM voucher v
            WHERE v.trang_thai = :status
            AND v.ngay_ket_thuc > NOW()
            AND v.so_luong_phat_hanh > v.so_luong_da_dung
            """, nativeQuery = true)
    List<Voucher> findExchangeableVouchers(@Param("status") String status);

    @Query(value = """
            SELECT * FROM voucher WHERE id = :voucherId
            """, nativeQuery = true)
    Optional<Voucher> findByIdForExchange(@Param("voucherId") Long voucherId);

    @Query("""
            SELECT v FROM Voucher v
            WHERE v.trangThaiUuDai = :status
            ORDER BY v.createdAt DESC
            """)
    List<Voucher> findByTrangThai(@Param("status") TrangThaiUuDai status);
}
