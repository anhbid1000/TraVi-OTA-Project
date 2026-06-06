package com.ota.travi.repository;

import com.ota.travi.entity.SoThichNguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SoThichNguoiDungRepository extends JpaRepository<SoThichNguoiDung, Long> {
    // Lấy tất cả sở thích của user
    List<SoThichNguoiDung> findByUser_IdOrderByDiemSoDesc(String userId);

    // Lấy sở thích cụ thể của user
    Optional<SoThichNguoiDung> findByUser_IdAndDanhMuc(String userId, String danhMuc);

    // Lấy top N sở thích của user (có điểm cao nhất)
    @Query("SELECT s FROM SoThichNguoiDung s WHERE s.user.id = :userId ORDER BY s.diemSo DESC LIMIT :limit")
    List<SoThichNguoiDung> findTopPreferencesByUser(
            @Param("userId") String userId,
            @Param("limit") int limit
    );

    // Kiểm tra user có sở thích nào không
    boolean existsByUser_Id(String userId);

    // Xóa tất cả sở thích của user
    void deleteByUser_Id(String userId);
}
