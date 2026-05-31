package com.ota.travi.entity;

import com.ota.travi.enums.AttachmentFileType;
import com.ota.travi.enums.AttachmentOwnerType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(
    name = "dinh_kem_phan_hoi",
    indexes = {
        @Index(name = "idx_attachment_owner", columnList = "owner_type, owner_id"),
        @Index(name = "idx_attachment_uploaded_by", columnList = "uploaded_by_id, uploaded_by_role")
    }
)
public class FeedbackAttachment {

    @Id
    @Column(length = 36)
    private String id;

    // Loại đối tượng sở hữu file: REVIEW, COMPLAINT hoặc COMPLAINT_MESSAGE.
    // Dùng polymorphic association để một bảng attachment phục vụ nhiều flow trong Module 5.
    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type", nullable = false, length = 50)
    private AttachmentOwnerType ownerType;

    // Id của review_danh_gia, complaint_khieu_nai hoặc complaint_tin_nhan.
    // Không tạo FK cứng vì owner_id trỏ đến nhiều bảng khác nhau; Service phải validate owner tồn tại.
    @Column(name = "owner_id", nullable = false, length = 36)
    private String ownerId;

    // URL/path public hoặc protected mà AttachmentService/FileStorageService trả về.
    @Column(name = "file_url", nullable = false, columnDefinition = "TEXT")
    private String fileUrl;

    // Tên file đã chuẩn hóa khi lưu. Không tin tuyệt đối tên gốc client gửi lên.
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false, length = 50)
    private AttachmentFileType fileType;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    // Người upload lấy từ SecurityContext, không lấy từ request body.
    @Column(name = "uploaded_by_id", nullable = false, length = 36)
    private String uploadedById;

    // KHACH_HANG hoặc DOI_TAC để audit và check quyền hiển thị.
    @Column(name = "uploaded_by_role", nullable = false, length = 50)
    private String uploadedByRole;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UUID.randomUUID().toString();
        createdAt = LocalDateTime.now();
    }
}
