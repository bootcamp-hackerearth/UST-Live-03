package com.ust.pos.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Component
public class FileStorageUtil {
    private static final Logger log = LoggerFactory.getLogger(FileStorageUtil.class);
    @Value("${app.upload.brand-path}")
    private String uploadRoot;

    public String saveBrandIcon(MultipartFile file, String identifier) {
        if (file == null || file.isEmpty()) return null;
        try {
            Path brandDir = Path.of(uploadRoot, "brands");
            Files.createDirectories(brandDir);
            String fileName = identifier + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = brandDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return "brands/" + fileName;
        } catch (IOException e) {
            log.error("Failed to store brand icon for identifier: {}", identifier, e);
        }
        return " ";
    }
}