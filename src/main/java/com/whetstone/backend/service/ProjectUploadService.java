package com.whetstone.backend.service;

import com.whetstone.backend.model.AnalysisProject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;


@Service 
public class ProjectUploadService {
    public AnalysisProject processProject(MultipartFile file) throws IOException {
        // Implementation for processing the uploaded project file goes here
        validateFile(file);

        Map<String, String> fileContents = new LinkedHashMap<>();


        try (InputStream inputStream = file.getInputStream(); ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry entry;

            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }

                String fileName = entry.getName();

                if (shouldIgnore(fileName)) {
                    continue;
                }

                byte[] bytes = zipInputStream.readAllBytes();
                fileContents.put(fileName, new String(bytes, StandardCharsets.UTF_8));

            }
        }

        String projectName = removeExtension(file.getOriginalFilename());

        return new AnalysisProject(projectName, fileContents);


    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("A project ZIP file is required.");
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null || !fileName.toLowerCase().endsWith(".zip")) {
            throw new IllegalArgumentException("Only ZIP files are supported.");
        }
    }

    private boolean shouldIgnore(String fileName) {
        String normalized = "/" + fileName.replace('\\', '/');
        return normalized.contains("/.git/") 
            || normalized.contains("/target/") 
            || normalized.contains("/node_modules/") 
            || normalized.contains("/.idea/") 
            || normalized.contains("/build/");
    }

    private String removeExtension(String fileName) {
        if (fileName == null) {
            return "unknown_project";
        }

        int extensionIndex = fileName.lastIndexOf('.');

        if (extensionIndex == -1) {
            return fileName;
        }

        return fileName.substring(0, extensionIndex);

    }

}
