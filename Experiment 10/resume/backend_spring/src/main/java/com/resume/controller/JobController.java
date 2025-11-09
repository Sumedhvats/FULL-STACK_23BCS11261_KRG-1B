package com.resume.controller;

import com.resume.dto.JobDTO;
import com.resume.dto.JobResponse;
import com.resume.dto.JobStatsResponse;
import com.resume.model.Job;
import com.resume.service.JobService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = {"http://localhost:8081", "https://your-frontend-domain.com"})
public class JobController {

    private final JobService jobService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createJob(@Valid @RequestBody JobDTO jobDTO) {
        Job job = jobService.createJob(jobDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Job created successfully");
        response.put("job", job);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<JobResponse> getAllJobs(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String experienceLevel,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String company,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "2000") @Min(1) @Max(2000) int limit) {

        Map<String, String> filters = new HashMap<>();
        if (search != null) filters.put("search", search);
        if (jobType != null) filters.put("jobType", jobType);
        if (experienceLevel != null) filters.put("experienceLevel", experienceLevel);
        if (location != null) filters.put("location", location);
        if (company != null) filters.put("company", company);

        JobResponse response = jobService.getAllJobs(filters, page, limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<JobStatsResponse> getJobStats() {
        JobStatsResponse stats = jobService.getJobStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJob(@PathVariable String id) {
        Job job = jobService.getJob(id);
        return ResponseEntity.ok(job);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateJob(
            @PathVariable String id,
            @Valid @RequestBody JobDTO jobDTO) {
        Job job = jobService.updateJob(id, jobDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Job updated successfully");
        response.put("job", job);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteJob(@PathVariable String id) {
        jobService.deleteJob(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Job deleted successfully");

        return ResponseEntity.ok(response);
    }
}