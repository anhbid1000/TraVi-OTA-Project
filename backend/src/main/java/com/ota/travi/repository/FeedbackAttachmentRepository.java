package com.ota.travi.repository;

import com.ota.travi.entity.FeedbackAttachment;
import com.ota.travi.enums.AttachmentOwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackAttachmentRepository extends JpaRepository<FeedbackAttachment, String> {

    List<FeedbackAttachment> findByOwnerTypeAndOwnerIdOrderByCreatedAtAsc(
        AttachmentOwnerType ownerType,
        String ownerId
    );

    long countByOwnerTypeAndOwnerId(AttachmentOwnerType ownerType, String ownerId);
}
