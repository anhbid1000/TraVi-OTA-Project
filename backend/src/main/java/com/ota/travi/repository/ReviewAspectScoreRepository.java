package com.ota.travi.repository;

import com.ota.travi.entity.ReviewAspectScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewAspectScoreRepository extends JpaRepository<ReviewAspectScore, String> {
    List<ReviewAspectScore> findByReview_Id(String reviewId);
}
