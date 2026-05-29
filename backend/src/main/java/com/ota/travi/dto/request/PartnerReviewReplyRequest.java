package com.ota.travi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request đối tác phản hồi review.
 */
public record PartnerReviewReplyRequest(
        @NotBlank(message = "Nội dung phản hồi không được để trống")
        @Size(max = 1000, message = "Nội dung tối đa 1000 ký tự")
        String noiDung
) {
}
