package com.ota.travi.repository;

import com.ota.travi.entity.DoiTac;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoiTacRepository extends JpaRepository<DoiTac, String> {
    Optional<DoiTac> findByEmail(String email);

    Optional<DoiTac> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
