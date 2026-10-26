package com.alamin.ecommerce.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public String saveFile(MultipartFile file) {
        if (file.isEmpty()) {
            System.out.println("File is empty. Please upload a valid file.");
            return null;
        }
        String fileName = file.getOriginalFilename();
        String uploadDir = "C:\\uploads\\";
        String fileExtension = "";
        if (fileName != null && fileName.contains(".")) {
            fileExtension = fileName.substring(fileName.lastIndexOf("."));
        }

        fileName = UUID.randomUUID().toString().replace("-", "") + fileExtension;
        try {
            File destinationFile = new File(uploadDir + fileName);
            file.transferTo(destinationFile);
            System.out.println("File saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save the file.");
        }
        return fileName;
    }

    public String saveFile(byte[] bytes, String fileName) throws IOException {
        Path path = null;
        String strPath = "uploads/" + fileName;

        // Check file size before saving
        if (bytes.length > 5 * 1024 * 1024) { // 5MB
            throw new IOException("File size exceeds the maximum limit of 5MB.");
        }

        try {
            // Save the file to a directory
            path = Paths.get(strPath);
            Files.write(path, bytes);
        } catch (IOException e) {
            log.error("Error saving file " + fileName, e);
            throw new IOException("Error saving file: " + fileName);
        }

        return strPath;
    }
}
