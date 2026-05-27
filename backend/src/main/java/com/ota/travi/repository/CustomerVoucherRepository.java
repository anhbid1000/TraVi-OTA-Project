package com.ota.travi.repository;

import com.ota.travi.entity.CustomerVoucher;
import com.ota.travi.enums.TrangThaiCustomerVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerVoucherRepository extends JpaRepository<CustomerVoucher, Long> {
    List<CustomerVoucher> findByKhachHang_IdAndTrangThaiOrderByIssuedAtDesc(
            String khachHangId,
            TrangThaiCustomerVoucher trangThai
    );
}
