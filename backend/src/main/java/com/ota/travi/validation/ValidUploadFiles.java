package com.ota.travi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validation cho danh sách file upload (multipart).
 * Dùng cho Phase 3.4: check số lượng file, mimeType, size.
 */
@Documented
@Constraint(validatedBy = ValidUploadFilesValidator.class)
@Target({ PARAMETER, FIELD })
@Retention(RUNTIME)
public @interface ValidUploadFiles {

    String message() default "File upload không hợp lệ";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int maxFiles() default 5;

    String[] allowedImageTypes() default {"image/jpeg", "image/png", "image/webp"};

    String[] allowedPdfTypes() default {"application/pdf"};

    boolean allowPdf() default false;

    long maxImageSizeBytes() default 5L * 1024 * 1024; // 5MB

    long maxPdfSizeBytes() default 10L * 1024 * 1024; // 10MB
}
