package com.ota.travi.repository;

import com.ota.travi.entity.KhachHang;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, String> {
    Optional<KhachHang> findById(String id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT k FROM KhachHang k WHERE k.id = :id")
    Optional<KhachHang> findByIdForUpdate(@Param("id") String id);
}
