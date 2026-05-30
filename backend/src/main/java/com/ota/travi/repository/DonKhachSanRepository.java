package com.ota.travi.repository;

import com.ota.travi.entity.DonKhachSan;
import com.ota.travi.enums.TrangThaiDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DonKhachSanRepository extends JpaRepository<DonKhachSan, String> {
    boolean existsByMaDon(String maDon);

    Optional<DonKhachSan> findByIdAndKhachHang_Username(String id, String username);

    List<DonKhachSan> findByTrangThaiDonAndPaymentExpiredAtBeforeAndDeletedFalse(
            TrangThaiDon trangThaiDon,
            LocalDateTime dateTime
    );
}
