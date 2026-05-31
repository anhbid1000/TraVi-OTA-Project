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
public interface BookingDonDatChoRepository extends JpaRepository<DonDatCho, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM BookingDonDatCho d WHERE d.id = :id")
    Optional<DonDatCho> findByIdWithLock(@Param("id") Long id);
}
