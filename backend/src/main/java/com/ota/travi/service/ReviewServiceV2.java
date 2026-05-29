package com.ota.travi.service;

import com.ota.travi.dto.request.PartnerReviewReplyRequest;
import com.ota.travi.dto.request.ReviewCreateRequest;
import com.ota.travi.dto.response.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewServiceV2 {

    ReviewResponse createReview(String customerId, ReviewCreateRequest request);

    ReviewResponse upsertPartnerReply(String partnerId, String reviewId, PartnerReviewReplyRequest request);

    Page<ReviewResponse> getPublicReviews(String businessProfileId, Pageable pageable);

    Page<ReviewResponse> getCustomerReviews(String customerId, Pageable pageable);
}
