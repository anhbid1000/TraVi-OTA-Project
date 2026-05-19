package com.ota.travi.booking.repository;

import com.ota.travi.booking.domain.entities.DonDatCho;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DonDatChoRepository extends JpaRepository<DonDatCho, Long> {

    // Đây chính là Best Practice 1 (Task 6.1): Khóa dòng dữ liệu (Pessimistic Locking) chống Overbooking
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM DonDatCho d WHERE d.id = :id")
    Optional<DonDatCho> findByIdWithLock(@Param("id") Long id);
}
