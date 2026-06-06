package com.ota.travi.repository;

import com.ota.travi.entity.ReviewReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewReplyRepository extends JpaRepository<ReviewReply, String> {
    Optional<ReviewReply> findByReview_Id(String reviewId);
    boolean existsByReview_Id(String reviewId);
}
