package com.resume.controller;

import com.resume.dto.MatchResponse;
import com.resume.dto.ResumeResponse;
import com.resume.model.Resume;
import com.resume.service.ResumeService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/resumes")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = {"http://localhost:8081", "https://your-frontend-domain.com"})
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(@RequestParam("resume") MultipartFile file) {
        try {
            Map<String, Object> response = resumeService.uploadResume(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to process resume");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping
    public ResponseEntity<ResumeResponse> getAllResumes(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "100") @Min(1) @Max(100) int limit) {

        ResumeResponse response = resumeService.getAllResumes(page, limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resume> getResume(@PathVariable String id) {
        Resume resume = resumeService.getResume(id);
        return ResponseEntity.ok(resume);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteResume(@PathVariable String id) {
        try {
            resumeService.deleteResume(id);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Resume deleted successfully");

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete resume");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/{id}/match")
    public ResponseEntity<MatchResponse> matchJobs(
            @PathVariable String id,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int limit) {

        MatchResponse response = resumeService.matchJobs(id, limit);
        return ResponseEntity.ok(response);
    }
}