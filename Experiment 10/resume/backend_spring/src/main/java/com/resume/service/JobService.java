package com.resume.service;

import com.resume.dto.JobDTO;
import com.resume.dto.JobResponse;
import com.resume.dto.JobStatsResponse;
import com.resume.model.Job;
import com.resume.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    public Job createJob(JobDTO jobDTO) {
        Job job = mapToEntity(jobDTO);

        if (job.getKeywords() != null) {
            job.setKeywords(job.getKeywords().stream()
                    .map(String::toLowerCase)
                    .map(String::trim)
                    .collect(Collectors.toList()));
        }

        if (job.getRequiredSkills() != null) {
            job.setRequiredSkills(job.getRequiredSkills().stream()
                    .map(String::toLowerCase)
                    .map(String::trim)
                    .collect(Collectors.toList()));
        }

        if (job.getPreferredSkills() != null) {
            job.setPreferredSkills(job.getPreferredSkills().stream()
                    .map(String::toLowerCase)
                    .map(String::trim)
                    .collect(Collectors.toList()));
        }

        return jobRepository.save(job);
    }

    public JobResponse getAllJobs(Map<String, String> filters, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "postedAt"));
        Page<Job> jobPage;

        String jobType = filters.get("jobType");
        String experienceLevel = filters.get("experienceLevel");
        String location = filters.get("location");
        String company = filters.get("company");
        String search = filters.get("search");

        if (search != null && !search.isEmpty()) {
            jobPage = jobRepository.findByTextSearch(search, pageable);
        } else if (jobType != null && experienceLevel != null) {
            jobPage = jobRepository.findByIsActiveAndJobTypeAndExperienceLevel(true, jobType, experienceLevel, pageable);
        } else if (jobType != null) {
            jobPage = jobRepository.findByIsActiveAndJobType(true, jobType, pageable);
        } else if (experienceLevel != null) {
            jobPage = jobRepository.findByIsActiveAndExperienceLevel(true, experienceLevel, pageable);
        } else if (location != null) {
            jobPage = jobRepository.findByIsActiveAndLocationRegex(true, location, pageable);
        } else if (company != null) {
            jobPage = jobRepository.findByIsActiveAndCompanyRegex(true, company, pageable);
        } else {
            jobPage = jobRepository.findByIsActive(true, pageable);
        }

        return new JobResponse(
                jobPage.getContent(),
                new JobResponse.PaginationInfo(page, limit, jobPage.getTotalElements(), jobPage.getTotalPages()),
                filters
        );
    }

    public Job getJob(String id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }

    public Job updateJob(String id, JobDTO jobDTO) {
        Job existingJob = getJob(id);
        Job updatedJob = mapToEntity(jobDTO);
        updatedJob.setId(existingJob.getId());
        updatedJob.setPostedAt(existingJob.getPostedAt());
        updatedJob.setIsActive(existingJob.getIsActive());

        if (updatedJob.getKeywords() != null) {
            updatedJob.setKeywords(updatedJob.getKeywords().stream()
                    .map(String::toLowerCase)
                    .map(String::trim)
                    .collect(Collectors.toList()));
        }

        if (updatedJob.getRequiredSkills() != null) {
            updatedJob.setRequiredSkills(updatedJob.getRequiredSkills().stream()
                    .map(String::toLowerCase)
                    .map(String::trim)
                    .collect(Collectors.toList()));
        }

        if (updatedJob.getPreferredSkills() != null) {
            updatedJob.setPreferredSkills(updatedJob.getPreferredSkills().stream()
                    .map(String::toLowerCase)
                    .map(String::trim)
                    .collect(Collectors.toList()));
        }

        return jobRepository.save(updatedJob);
    }

    public void deleteJob(String id) {
        Job job = getJob(id);
        job.setIsActive(false);
        jobRepository.save(job);
    }

    public JobStatsResponse getJobStats() {
        List<Job> activeJobs = jobRepository.findTop50ByIsActiveOrderByPostedAtDesc(true);

        if (activeJobs.isEmpty()) {
            return new JobStatsResponse(0, new HashMap<>(), new HashMap<>(),
                    new JobStatsResponse.AverageSalary(0, 0));
        }

        long totalJobs = jobRepository.countByIsActive(true);

        Map<String, Long> jobTypeCounts = activeJobs.stream()
                .filter(job -> job.getJobType() != null)
                .collect(Collectors.groupingBy(Job::getJobType, Collectors.counting()));

        Map<String, Long> experienceCounts = activeJobs.stream()
                .filter(job -> job.getExperienceLevel() != null)
                .collect(Collectors.groupingBy(Job::getExperienceLevel, Collectors.counting()));

        double avgMin = activeJobs.stream()
                .filter(job -> job.getSalaryRange() != null && job.getSalaryRange().getMin() != null)
                .mapToInt(job -> job.getSalaryRange().getMin())
                .average()
                .orElse(0.0);

        double avgMax = activeJobs.stream()
                .filter(job -> job.getSalaryRange() != null && job.getSalaryRange().getMax() != null)
                .mapToInt(job -> job.getSalaryRange().getMax())
                .average()
                .orElse(0.0);

        return new JobStatsResponse(
                totalJobs,
                jobTypeCounts,
                experienceCounts,
                new JobStatsResponse.AverageSalary(Math.round(avgMin), Math.round(avgMax))
        );
    }

    private Job mapToEntity(JobDTO dto) {
        Job job = new Job();
        job.setTitle(dto.getTitle() != null ? dto.getTitle().trim() : null);
        job.setCompany(dto.getCompany() != null ? dto.getCompany().trim() : null);
        job.setDescription(dto.getDescription());
        job.setRequirements(dto.getRequirements());
        job.setLocation(dto.getLocation() != null ? dto.getLocation().trim() : null);
        job.setJobType(dto.getJobType());
        job.setKeywords(dto.getKeywords());
        job.setRequiredSkills(dto.getRequiredSkills());
        job.setPreferredSkills(dto.getPreferredSkills());
        job.setExperienceLevel(dto.getExperienceLevel());
        job.setApplicationUrl(dto.getApplicationUrl());
        job.setContactEmail(dto.getContactEmail());
        job.setExpiresAt(dto.getExpiresAt());

        if (dto.getSalaryRange() != null) {
            Job.SalaryRange salaryRange = new Job.SalaryRange();
            salaryRange.setMin(dto.getSalaryRange().getMin());
            salaryRange.setMax(dto.getSalaryRange().getMax());
            salaryRange.setCurrency(dto.getSalaryRange().getCurrency());
            job.setSalaryRange(salaryRange);
        }

        return job;
    }
}