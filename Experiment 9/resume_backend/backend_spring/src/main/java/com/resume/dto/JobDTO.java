package com.resume.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class JobDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @NotBlank(message = "Company is required")
    @Size(max = 200, message = "Company must not exceed 200 characters")
    private String company;

    @NotBlank(message = "Description is required")
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    @NotBlank(message = "Requirements are required")
    @Size(max = 3000, message = "Requirements must not exceed 3000 characters")
    private String requirements;

    @Size(max = 200, message = "Location must not exceed 200 characters")
    private String location;

    @Valid
    private SalaryRangeDTO salaryRange;

    @Pattern(regexp = "full-time|part-time|contract|internship|freelance",
            message = "Job type must be one of: full-time, part-time, contract, internship, freelance")
    private String jobType;

    private List<String> keywords;

    private List<String> requiredSkills;

    private List<String> preferredSkills;

    @Pattern(regexp = "entry|mid|senior|lead|executive",
            message = "Experience level must be one of: entry, mid, senior, lead, executive")
    private String experienceLevel;

    private String applicationUrl;

    @Email(message = "Invalid email format")
    private String contactEmail;

    @Future(message = "Expiration date must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiresAt;

    @Data
    public static class SalaryRangeDTO {
        @Min(value = 0, message = "Minimum salary must be non-negative")
        private Integer min;

        @Min(value = 0, message = "Maximum salary must be non-negative")
        private Integer max;

        private String currency = "USD";
    }
}