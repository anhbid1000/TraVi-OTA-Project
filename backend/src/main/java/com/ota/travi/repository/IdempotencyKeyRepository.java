package com.ota.travi.repository;

import com.ota.travi.entity.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, Long> {
    Optional<IdempotencyKey> findByIdempotencyKey(String idempotencyKey);

    @Query("DELETE FROM IdempotencyKey ik WHERE ik.expiresAt IS NOT NULL AND ik.expiresAt < :now")
    void deleteExpiredKeys(@Param("now") LocalDateTime now);
}
