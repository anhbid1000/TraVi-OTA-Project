package com.ota.travi.repository;

import com.ota.travi.entity.UuDai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UuDaiRepository extends JpaRepository<UuDai, Long> {
    List<UuDai> findByDeletedFalseOrderByCreatedAtDesc();

    List<UuDai> findByCreatedByUserIdAndDeletedFalseOrderByCreatedAtDesc(String createdByUserId);
}
