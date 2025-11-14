package com.resume.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "resumes")
public class Resume {

    @Id
    private String id;

    private String originalName;

    private String filename;

    private String filePath;

    @TextIndexed
    private String extractedText;

    @Indexed
    private List<String> keywords;

    private List<String> skills;

    private String experience;

    private String education;

    private ContactInfo contactInfo;

    private Long fileSize;

    private String mimeType;

    @Indexed
    private LocalDateTime uploadedAt = LocalDateTime.now();

    private List<MatchHistory> matchHistory = new ArrayList<>();

    @Data
    public static class ContactInfo {
        private String email;
        private String phone;
        private String name;
    }

    @Data
    public static class MatchHistory {
        private String jobId;
        private Double score;
        private List<String> matchedKeywords;
        private LocalDateTime matchedAt = LocalDateTime.now();
    }
}