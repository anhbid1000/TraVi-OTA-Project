package com.ota.travi.repository;

import com.ota.travi.entity.VaiTro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VaiTroRepository extends JpaRepository<VaiTro, String> {
    Optional<VaiTro> findByTen(String ten);

    boolean existsByTen(String ten);
}
