package com.ota.travi.repository;

import com.ota.travi.entity.AnhPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnhPhongRepository extends JpaRepository<AnhPhong, String> {
    List<AnhPhong> findByPhong_Id(String phongId);

    Optional<AnhPhong> findFirstByPhong_IdAndLaAnhDaiDienTrue(String phongId);

    void deleteByPhong_Id(String phongId);
}
