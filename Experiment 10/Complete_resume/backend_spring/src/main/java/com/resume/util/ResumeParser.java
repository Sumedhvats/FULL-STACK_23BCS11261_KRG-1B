package com.resume.util;

import com.resume.model.Resume;
import lombok.Data;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class ResumeParser {

    private static final List<String> COMMON_KEYWORDS = Arrays.asList(
            "javascript", "python", "java", "c++", "c#", "php", "ruby", "go", "rust", "swift",
            "kotlin", "scala", "typescript", "html", "css", "sql", "r", "matlab",
            "react", "angular", "vue", "node.js", "express", "django", "flask", "spring",
            "laravel", "rails", "jquery", "bootstrap", "tailwind",
            "mysql", "postgresql", "mongodb", "redis", "sqlite", "oracle", "cassandra",
            "aws", "azure", "gcp", "docker", "kubernetes", "jenkins", "git", "linux",
            "terraform", "ansible", "puppet", "chef",
            "agile", "scrum", "kanban", "ci/cd", "tdd", "bdd", "microservices", "api",
            "rest", "graphql", "machine learning", "ai", "data science", "analytics",
            "project management", "leadership", "team lead", "mentoring"
    );

    private static final List<String> TECHNICAL_SKILLS = Arrays.asList(
            "programming", "development", "software engineering", "web development",
            "mobile development", "database design", "system administration",
            "network administration", "cybersecurity", "data analysis", "testing",
            "debugging", "problem solving", "algorithm design", "architecture"
    );

    public ParseResult parseFile(String filePath, String mimeType) throws IOException {
        String extractedText;

        switch (mimeType) {
            case "application/pdf":
                extractedText = parsePDF(filePath);
                break;
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
            case "application/msword":
                extractedText = parseWord(filePath);
                break;
            case "text/plain":
                extractedText = parseText(filePath);
                break;
            default:
                throw new IllegalArgumentException("Unsupported file type: " + mimeType);
        }

        return analyzeText(extractedText);
    }

    private String parsePDF(String filePath) throws IOException {
        try (PDDocument document = org.apache.pdfbox.Loader.loadPDF(new java.io.File(filePath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String parseWord(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument document = new XWPFDocument(fis);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }

    private String parseText(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    private ParseResult analyzeText(String text) {
        String lowercaseText = text.toLowerCase();

        List<String> keywords = extractKeywords(lowercaseText);
        List<String> skills = extractSkills(lowercaseText);
        Resume.ContactInfo contactInfo = extractContactInfo(text);
        String experience = extractExperience(text);
        String education = extractEducation(text);

        ParseResult result = new ParseResult();
        result.setExtractedText(text);
        result.setKeywords(new ArrayList<>(new HashSet<>(keywords)));
        result.setSkills(new ArrayList<>(new HashSet<>(skills)));
        result.setContactInfo(contactInfo);
        result.setExperience(experience);
        result.setEducation(education);

        return result;
    }

    private List<String> extractKeywords(String text) {
        return COMMON_KEYWORDS.stream()
                .filter(text::contains)
                .collect(Collectors.toList());
    }

    private List<String> extractSkills(String text) {
        return TECHNICAL_SKILLS.stream()
                .filter(text::contains)
                .collect(Collectors.toList());
    }

    private Resume.ContactInfo extractContactInfo(String text) {
        Resume.ContactInfo contactInfo = new Resume.ContactInfo();

        Pattern emailPattern = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b");
        Matcher emailMatcher = emailPattern.matcher(text);
        if (emailMatcher.find()) {
            contactInfo.setEmail(emailMatcher.group());
        }

        Pattern phonePattern = Pattern.compile("(\\+?\\d{1,3}[-\\.\\s]?)?\\(?(\\d{3})\\)?[-\\.\\s]?(\\d{3})[-\\.\\s]?(\\d{4})");
        Matcher phoneMatcher = phonePattern.matcher(text);
        if (phoneMatcher.find()) {
            contactInfo.setPhone(phoneMatcher.group());
        }

        Pattern namePattern = Pattern.compile("^([A-Z][a-z]+ [A-Z][a-z]+)", Pattern.MULTILINE);
        Matcher nameMatcher = namePattern.matcher(text);
        if (nameMatcher.find()) {
            contactInfo.setName(nameMatcher.group());
        }

        return contactInfo;
    }

    private String extractExperience(String text) {
        Pattern experiencePattern = Pattern.compile("experience[\\s\\S]{0,500}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = experiencePattern.matcher(text);
        if (matcher.find()) {
            String section = matcher.group();
            return section.length() > 500 ? section.substring(0, 500) : section;
        }
        return null;
    }

    private String extractEducation(String text) {
        Pattern educationPattern = Pattern.compile("education[\\s\\S]{0,500}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = educationPattern.matcher(text);
        if (matcher.find()) {
            String section = matcher.group();
            return section.length() > 500 ? section.substring(0, 500) : section;
        }
        return null;
    }

    @Data
    public static class ParseResult {
        private String extractedText;
        private List<String> keywords;
        private List<String> skills;
        private Resume.ContactInfo contactInfo;
        private String experience;
        private String education;
    }
}