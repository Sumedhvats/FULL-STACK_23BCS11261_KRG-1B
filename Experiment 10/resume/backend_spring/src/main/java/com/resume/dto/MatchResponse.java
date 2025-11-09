package com.resume.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class MatchResponse {
    private String resumeId;
    private List<JobMatch> matches;

    @Data
    @AllArgsConstructor
    public static class JobMatch {
        private JobInfo job;
        private double score;
        private List<String> matchedKeywords;
        private ScoreBreakdown breakdown;
    }

    @Data
    @AllArgsConstructor
    public static class JobInfo {
        private String id;
        private String title;
        private String company;
        private String location;
        private String jobType;
        private String experienceLevel;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime postedAt;
    }

    @Data
    @AllArgsConstructor
    public static class ScoreBreakdown {
        private double keywords;
        private double skills;
        private double textSimilarity;
        private double experienceLevel;
    }
}