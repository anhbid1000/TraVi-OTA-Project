package com.ota.travi.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageInterfaceService {
    String store(MultipartFile file);
    Resource loadAsResource(String filename);
    void delete(String filename);
}
