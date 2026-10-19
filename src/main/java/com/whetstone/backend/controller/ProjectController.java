package com.whetstone.backend.controller;

import java.io.IOException;

import com.whetstone.backend.model.AnalysisProject;
import com.whetstone.backend.service.ProjectUploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectUploadService projectUploadService;

    public ProjectController(ProjectUploadService projectUploadService) {
        this.projectUploadService = projectUploadService;
    }

    @GetMapping
    public String home() {
        return "Whetstone API is running!";
    }

    @PostMapping("/upload")
    public ResponseEntity<AnalysisProject> uploadProject(@RequestParam("file") MultipartFile file) throws IOException {
        AnalysisProject project = projectUploadService.processProject(file);
        return ResponseEntity.ok(project);
    }

}