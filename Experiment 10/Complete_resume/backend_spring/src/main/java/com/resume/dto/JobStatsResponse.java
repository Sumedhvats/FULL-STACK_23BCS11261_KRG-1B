package com.resume.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class JobStatsResponse {
    private long totalJobs;
    private Map<String, Long> jobTypes;
    private Map<String, Long> experienceLevels;
    private AverageSalary averageSalary;

    @Data
    @AllArgsConstructor
    public static class AverageSalary {
        private long min;
        private long max;
    }
}