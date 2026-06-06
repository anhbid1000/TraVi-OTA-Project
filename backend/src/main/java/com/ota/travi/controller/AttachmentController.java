package com.ota.travi.controller;

import com.ota.travi.entity.Complaint;
import com.ota.travi.entity.ComplaintMessage;
import com.ota.travi.entity.FeedbackAttachment;
import com.ota.travi.entity.Review;
import com.ota.travi.enums.AttachmentOwnerType;
import com.ota.travi.enums.TrangThaiDanhGia;
import com.ota.travi.repository.ComplaintMessageRepository;
import com.ota.travi.repository.ComplaintRepository;
import com.ota.travi.repository.FeedbackAttachmentRepository;
import com.ota.travi.repository.ReviewRepository;
import com.ota.travi.security.CustomUserDetails;
import com.ota.travi.service.FileStorageInterfaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;

import static com.ota.travi.constant.ApiEndpoints.BASE_PREFIX;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * API truy cập file đính kèm cho Module 5.
 * Fit-project: dùng local storage + security check theo ownerType/ownerId.
 */
@RestController
@RequestMapping(BASE_PREFIX + "/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final FeedbackAttachmentRepository attachmentRepository;
    private final ReviewRepository reviewRepository;
    private final ComplaintRepository complaintRepository;
    private final ComplaintMessageRepository complaintMessageRepository;
    private final com.ota.travi.repository.ComplaintResolutionActionRepository resolutionActionRepository;
    private final FileStorageInterfaceService fileStorageService;

    @GetMapping("/{attachmentId}")
    public ResponseEntity<Resource> getAttachment(@PathVariable String attachmentId) {
        FeedbackAttachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Không tìm thấy file đính kèm"));

        if (!canAccessAttachment(attachment)) {
            throw new ResponseStatusException(FORBIDDEN, "Bạn không có quyền truy cập file này");
        }

        Resource resource = fileStorageService.loadAsResource(attachment.getFileUrl());

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        try {
            if (attachment.getMimeType() != null && !attachment.getMimeType().isBlank()) {
                mediaType = MediaType.parseMediaType(attachment.getMimeType());
            }
        } catch (Exception ignored) {
            // Fallback application/octet-stream nếu mimeType không parse được.
        }

        ContentDisposition disposition = ContentDisposition.inline()
                .filename(attachment.getFileName(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(resource);
    }

    private boolean canAccessAttachment(FeedbackAttachment attachment) {
        if (attachment.getOwnerType() == AttachmentOwnerType.REVIEW) {
            return canAccessReviewAttachment(attachment.getOwnerId());
        }
        if (attachment.getOwnerType() == AttachmentOwnerType.COMPLAINT) {
            Complaint complaint = complaintRepository.findById(attachment.getOwnerId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Không tìm thấy complaint của file đính kèm"));
            return canAccessComplaint(complaint);
        }
        if (attachment.getOwnerType() == AttachmentOwnerType.COMPLAINT_MESSAGE) {
            ComplaintMessage message = complaintMessageRepository.findById(attachment.getOwnerId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Không tìm thấy message của file đính kèm"));
            return canAccessComplaint(message.getComplaint());
        }
        if (attachment.getOwnerType() == AttachmentOwnerType.RESOLUTION_ACTION) {
            com.ota.travi.entity.ComplaintResolutionAction action = resolutionActionRepository.findById(attachment.getOwnerId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Không tìm thấy action của file đính kèm"));
            return canAccessComplaint(action.getComplaint());
        }
        return false;
    }

    private boolean canAccessReviewAttachment(String reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Không tìm thấy review của file đính kèm"));

        // Review công khai thì cho public xem (fit-project).
        if (review.getTrangThai() == TrangThaiDanhGia.DA_HIEN_THI) {
            return true;
        }

        AuthUser authUser = getCurrentUser();
        if (authUser == null) {
            return false;
        }

        // Review BI_AN: chỉ customer tạo review hoặc partner sở hữu cơ sở được xem.
        if (review.getKhachHang().getId().equals(authUser.userId())) {
            return true;
        }
        return review.getHoSoKinhDoanh().getDoiTac().getId().equals(authUser.userId());
    }

    private boolean canAccessComplaint(Complaint complaint) {
        AuthUser authUser = getCurrentUser();
        if (authUser == null) {
            return false;
        }

        if (complaint.getKhachHang().getId().equals(authUser.userId())) {
            return true;
        }
        return complaint.getHoSoKinhDoanh().getDoiTac().getId().equals(authUser.userId());
    }

    private AuthUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return new AuthUser(userDetails.getUser().getId(), userDetails.getUser().getVaiTro().getTen());
        }
        return null;
    }

    private record AuthUser(String userId, String role) {
    }
}
