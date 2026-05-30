package com.ota.travi.service;

import com.ota.travi.entity.FeedbackAttachment;
import com.ota.travi.enums.AttachmentOwnerType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AttachmentService {
    
    /**
     * Validate and save attachments for a Review.
     * Rule: Max 5 files, IMAGE only, max 5MB/file.
     */
    List<FeedbackAttachment> saveReviewAttachments(String reviewId, String uploadedById, String uploadedByRole, List<MultipartFile> files);

    /**
     * Validate and save attachments for a Complaint.
     * Rule: Max 10 files, IMAGE (5MB) or PDF (10MB).
     */
    List<FeedbackAttachment> saveComplaintAttachments(String complaintId, String uploadedById, String uploadedByRole, List<MultipartFile> files);

    /**
     * Validate and save attachments for a ComplaintMessage.
     * Rule: Max 5 files, IMAGE (5MB) or PDF (10MB).
     */
    List<FeedbackAttachment> saveComplaintMessageAttachments(String messageId, String uploadedById, String uploadedByRole, List<MultipartFile> files);

    /**
     * Fetch attachments for a specific owner.
     */
    List<FeedbackAttachment> getAttachmentsByOwner(AttachmentOwnerType ownerType, String ownerId);
    
    /**
     * Delete an attachment by ID (and physical file).
     */
    void deleteAttachment(String attachmentId);
}
