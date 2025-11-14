package com.resume.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "jobs")
public class Job {

    @Id
    private String id;

    @TextIndexed
    private String title;

    private String company;

    @TextIndexed
    private String description;

    @TextIndexed
    private String requirements;

    private String location;

    private SalaryRange salaryRange;

    private String jobType = "full-time";

    @Indexed
    private List<String> keywords;

    private List<String> requiredSkills;

    private List<String> preferredSkills;

    private String experienceLevel = "mid";

    @Indexed
    private Boolean isActive = true;

    @Indexed
    private LocalDateTime postedAt = LocalDateTime.now();

    private LocalDateTime expiresAt;

    private String applicationUrl;

    private String contactEmail;

    @Data
    public static class SalaryRange {
        private Integer min;
        private Integer max;
        private String currency = "USD";
    }
}