package com.alamin.ecommerce.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class FileUploadService {
    public String uploadFile(MultipartFile file){
        if (file.isEmpty()) {
            log.error("File is empty. Please upload a valid file.: {}", file);
            return null;
        }
        String fileName = file.getOriginalFilename();
        String uploadDir = "C:\\uploads\\";
        String fileExtension = "";
        if (fileName != null && fileName.contains(".")) {
            fileExtension = fileName.substring(fileName.lastIndexOf("."));
        } else {
            log.warn("no file extension: {}", fileName);
        }

        fileName = UUID.randomUUID().toString().replace("-", "") + fileExtension;
        try {
            File destinationFile = new File(uploadDir + fileName);
            file.transferTo(destinationFile);
            log.info("File saved successfully.");
        } catch (IOException e) {
            log.error("Failed to save the file. :", e);
        }
        return fileName;
    }
}
