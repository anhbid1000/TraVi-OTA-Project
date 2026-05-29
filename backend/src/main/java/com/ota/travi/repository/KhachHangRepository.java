package com.ota.travi.repository;

import com.ota.travi.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, String> {
    // Có thể thêm các phương thức tìm kiếm đặc thù cho KhachHang nếu cần
    Optional<KhachHang> findById(String id);
}
