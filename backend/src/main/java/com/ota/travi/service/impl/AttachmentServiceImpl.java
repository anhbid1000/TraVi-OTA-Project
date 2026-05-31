package com.ota.travi.service.impl;

import com.ota.travi.entity.FeedbackAttachment;
import com.ota.travi.enums.AttachmentFileType;
import com.ota.travi.enums.AttachmentOwnerType;
import com.ota.travi.exception.FileValidationException;
import com.ota.travi.repository.FeedbackAttachmentRepository;
import com.ota.travi.service.AttachmentService;
import com.ota.travi.service.FileStorageInterfaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttachmentServiceImpl implements AttachmentService {

    private final FileStorageInterfaceService fileStorageService;
    private final FeedbackAttachmentRepository attachmentRepository;

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final long MAX_PDF_SIZE = 10 * 1024 * 1024; // 10MB
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final Set<String> ALLOWED_PDF_TYPES = Set.of("application/pdf");

    @Override
    @Transactional
    public List<FeedbackAttachment> saveReviewAttachments(String reviewId, String uploadedById, String uploadedByRole, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return new ArrayList<>();
        
        if (files.size() > 5) {
            throw new FileValidationException("Review cho phép đính kèm tối đa 5 file.");
        }

        List<FeedbackAttachment> attachments = new ArrayList<>();
        for (MultipartFile file : files) {
            validateImage(file);
            attachments.add(processAndSaveFile(file, AttachmentOwnerType.REVIEW, reviewId, uploadedById, uploadedByRole));
        }
        return attachments;
    }

    @Override
    @Transactional
    public List<FeedbackAttachment> saveComplaintAttachments(String complaintId, String uploadedById, String uploadedByRole, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return new ArrayList<>();
        
        if (files.size() > 10) {
            throw new FileValidationException("Khiếu nại cho phép đính kèm tối đa 10 file.");
        }

        List<FeedbackAttachment> attachments = new ArrayList<>();
        for (MultipartFile file : files) {
            validateImageOrPdf(file);
            attachments.add(processAndSaveFile(file, AttachmentOwnerType.COMPLAINT, complaintId, uploadedById, uploadedByRole));
        }
        return attachments;
    }

    @Override
    @Transactional
    public List<FeedbackAttachment> saveComplaintMessageAttachments(String messageId, String uploadedById, String uploadedByRole, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return new ArrayList<>();
        
        if (files.size() > 5) {
            throw new FileValidationException("Tin nhắn khiếu nại cho phép đính kèm tối đa 5 file.");
        }

        List<FeedbackAttachment> attachments = new ArrayList<>();
        for (MultipartFile file : files) {
            validateImageOrPdf(file);
            attachments.add(processAndSaveFile(file, AttachmentOwnerType.COMPLAINT_MESSAGE, messageId, uploadedById, uploadedByRole));
        }
        return attachments;
    }

    @Override
    @Transactional
    public List<FeedbackAttachment> saveResolutionActionAttachments(String actionId, String uploadedById, String uploadedByRole, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return new ArrayList<>();
        if (files.size() > 5) throw new FileValidationException("Phương án xử lý cho phép đính kèm tối đa 5 file.");
        List<FeedbackAttachment> attachments = new ArrayList<>();
        for (MultipartFile file : files) {
            validateImageOrPdf(file);
            attachments.add(processAndSaveFile(file, AttachmentOwnerType.RESOLUTION_ACTION, actionId, uploadedById, uploadedByRole));
        }
        return attachments;
    }

    @Override
    public List<FeedbackAttachment> getAttachmentsByOwner(AttachmentOwnerType ownerType, String ownerId) {
        return attachmentRepository.findByOwnerTypeAndOwnerIdOrderByCreatedAtAsc(ownerType, ownerId);
    }

    @Override
    @Transactional
    public void deleteAttachment(String attachmentId) {
        attachmentRepository.findById(attachmentId).ifPresent(attachment -> {
            fileStorageService.delete(attachment.getFileUrl()); // fileUrl stores the filename in local storage
            attachmentRepository.delete(attachment);
        });
    }

    private FeedbackAttachment processAndSaveFile(MultipartFile file, AttachmentOwnerType ownerType, String ownerId, String uploadedById, String uploadedByRole) {
        // Store physical file
        String storedFileName = fileStorageService.store(file);
        
        // Determine type
        String mimeType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
        AttachmentFileType fileType = ALLOWED_IMAGE_TYPES.contains(mimeType) ? AttachmentFileType.IMAGE : 
                                      (ALLOWED_PDF_TYPES.contains(mimeType) ? AttachmentFileType.PDF : AttachmentFileType.OTHER);

        // Build entity
        FeedbackAttachment attachment = new FeedbackAttachment();
        attachment.setOwnerType(ownerType);
        attachment.setOwnerId(ownerId);
        attachment.setFileUrl(storedFileName); // For Local storage, url = filename. Can map to full URL in DTO mapping.
        attachment.setFileName(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));
        attachment.setFileType(fileType);
        attachment.setMimeType(mimeType);
        attachment.setFileSize(file.getSize());
        attachment.setUploadedById(uploadedById);
        attachment.setUploadedByRole(uploadedByRole);

        return attachmentRepository.save(attachment);
    }

    private void validateImage(MultipartFile file) {
        String mimeType = file.getContentType();
        if (mimeType == null || !ALLOWED_IMAGE_TYPES.contains(mimeType)) {
            throw new FileValidationException("Chỉ chấp nhận file định dạng IMAGE (JPEG, PNG, WEBP). File gửi lên: " + mimeType);
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new FileValidationException("Kích thước file ảnh vượt quá giới hạn 5MB.");
        }
    }

    private void validateImageOrPdf(MultipartFile file) {
        String mimeType = file.getContentType();
        if (mimeType == null) {
            throw new FileValidationException("Không thể xác định định dạng file.");
        }
        
        if (ALLOWED_IMAGE_TYPES.contains(mimeType)) {
            if (file.getSize() > MAX_IMAGE_SIZE) {
                throw new FileValidationException("Kích thước file ảnh vượt quá giới hạn 5MB.");
            }
        } else if (ALLOWED_PDF_TYPES.contains(mimeType)) {
            if (file.getSize() > MAX_PDF_SIZE) {
                throw new FileValidationException("Kích thước file PDF vượt quá giới hạn 10MB.");
            }
        } else {
            throw new FileValidationException("Chỉ chấp nhận định dạng IMAGE hoặc PDF. File gửi lên: " + mimeType);
        }
    }
}
