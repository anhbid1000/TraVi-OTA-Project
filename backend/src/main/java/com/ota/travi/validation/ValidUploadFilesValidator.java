package com.ota.travi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validator cho @ValidUploadFiles.
 */
public class ValidUploadFilesValidator implements ConstraintValidator<ValidUploadFiles, List<MultipartFile>> {

    private int maxFiles;
    private Set<String> allowedImageTypes;
    private Set<String> allowedPdfTypes;
    private boolean allowPdf;
    private long maxImageSizeBytes;
    private long maxPdfSizeBytes;

    @Override
    public void initialize(ValidUploadFiles annotation) {
        this.maxFiles = annotation.maxFiles();
        this.allowedImageTypes = new HashSet<>(Arrays.asList(annotation.allowedImageTypes()));
        this.allowedPdfTypes = new HashSet<>(Arrays.asList(annotation.allowedPdfTypes()));
        this.allowPdf = annotation.allowPdf();
        this.maxImageSizeBytes = annotation.maxImageSizeBytes();
        this.maxPdfSizeBytes = annotation.maxPdfSizeBytes();
    }

    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext context) {
        if (files == null || files.isEmpty()) {
            return true;
        }

        if (files.size() > maxFiles) {
            buildViolation(context, "Số lượng file vượt quá giới hạn cho phép: " + maxFiles);
            return false;
        }

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                buildViolation(context, "Không chấp nhận file rỗng");
                return false;
            }

            String mimeType = file.getContentType();
            if (mimeType == null || mimeType.isBlank()) {
                buildViolation(context, "Không thể xác định định dạng file");
                return false;
            }

            if (allowedImageTypes.contains(mimeType)) {
                if (file.getSize() > maxImageSizeBytes) {
                    buildViolation(context, "Kích thước file ảnh vượt quá giới hạn " + (maxImageSizeBytes / (1024 * 1024)) + "MB");
                    return false;
                }
                continue;
            }

            if (allowPdf && allowedPdfTypes.contains(mimeType)) {
                if (file.getSize() > maxPdfSizeBytes) {
                    buildViolation(context, "Kích thước file PDF vượt quá giới hạn " + (maxPdfSizeBytes / (1024 * 1024)) + "MB");
                    return false;
                }
                continue;
            }

            String acceptedTypes = allowPdf
                    ? "IMAGE (JPEG, PNG, WEBP) hoặc PDF"
                    : "IMAGE (JPEG, PNG, WEBP)";
            buildViolation(context, "Định dạng file không hợp lệ. Chỉ chấp nhận " + acceptedTypes);
            return false;
        }

        return true;
    }

    private void buildViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
