package com.ota.travi.service;

import com.ota.travi.dto.response.FileUploadResponse;
import com.ota.travi.exception.ValidationException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {
    private static final String UPLOAD_DIR = "uploads/partner-assets";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "application/pdf"
    );

    public FileUploadResponse storePartnerAsset(MultipartFile file) {
        validateFile(file);

        try {
            Path uploadPath = Path.of(UPLOAD_DIR).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String originalFileName = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
            String safeFileName = originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
            String storedFileName = UUID.randomUUID() + "-" + safeFileName;
            Path destination = uploadPath.resolve(storedFileName).normalize();
            if (!destination.startsWith(uploadPath)) {
                throw new ValidationException("Ten file tai len khong hop le");
            }

            file.transferTo(destination);

            return new FileUploadResponse(
                    storedFileName,
                    "/uploads/partner-assets/" + storedFileName,
                    file.getContentType(),
                    file.getSize()
            );
        } catch (IOException ex) {
            throw new ValidationException("Khong the luu file tai len");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("File tai len khong duoc de trong");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ValidationException("File tai len khong duoc vuot qua 10MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new ValidationException("Chi ho tro file PDF, JPG hoac PNG");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new ValidationException("Ten file tai len khong hop le");
        }

        String lowerFileName = originalFileName.toLowerCase(Locale.ROOT);
        boolean validExtension = lowerFileName.endsWith(".jpg")
                || lowerFileName.endsWith(".jpeg")
                || lowerFileName.endsWith(".png")
                || lowerFileName.endsWith(".pdf");
        if (!validExtension) {
            throw new ValidationException("Phan mo rong file khong hop le");
        }
    }
}
// import org.springframework.core.io.Resource;
// import org.springframework.web.multipart.MultipartFile;

// public interface FileStorageService {
//     String store(MultipartFile file);
//     Resource loadAsResource(String filename);
//     void delete(String filename);
// }
