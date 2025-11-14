package com.resume.service;

import com.resume.dto.MatchResponse;
import com.resume.dto.ResumeResponse;
import com.resume.model.Resume;
import com.resume.repository.ResumeRepository;
import com.resume.util.JobMatcher;
import com.resume.util.ResumeParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final ResumeParser resumeParser;
    private final JobMatcher jobMatcher;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public Map<String, Object> uploadResume(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("No file uploaded");
        }

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String filename = System.currentTimeMillis() + "-" +
                new Random().nextInt(1000000000) + "-" + originalFilename;
        Path filePath = uploadPath.resolve(filename);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        try {
            ResumeParser.ParseResult parseResult = resumeParser.parseFile(
                    filePath.toString(),
                    file.getContentType()
            );

            Resume resume = new Resume();
            resume.setOriginalName(originalFilename);
            resume.setFilename(filename);
            resume.setFilePath(filePath.toString());
            resume.setExtractedText(parseResult.getExtractedText());
            resume.setKeywords(parseResult.getKeywords());
            resume.setSkills(parseResult.getSkills());
            resume.setExperience(parseResult.getExperience());
            resume.setEducation(parseResult.getEducation());
            resume.setContactInfo(parseResult.getContactInfo());
            resume.setFileSize(file.getSize());
            resume.setMimeType(file.getContentType());

            resume = resumeRepository.save(resume);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Resume uploaded and parsed successfully");

            Map<String, Object> resumeData = new HashMap<>();
            resumeData.put("id", resume.getId());
            resumeData.put("originalName", resume.getOriginalName());
            resumeData.put("keywords", resume.getKeywords());
            resumeData.put("skills", resume.getSkills());
            resumeData.put("contactInfo", resume.getContactInfo());
            resumeData.put("uploadedAt", resume.getUploadedAt());

            response.put("resume", resumeData);

            return response;

        } catch (Exception e) {
            Files.deleteIfExists(filePath);
            throw new RuntimeException("Failed to process resume: " + e.getMessage(), e);
        }
    }

    public ResumeResponse getAllResumes(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "uploadedAt"));
        Page<Resume> resumePage = resumeRepository.findAllByOrderByUploadedAtDesc(pageable);

        List<Resume> resumes = resumePage.getContent().stream()
                .peek(resume -> {
                    resume.setExtractedText(null);
                    resume.setFilePath(null);
                })
                .collect(Collectors.toList());

        return new ResumeResponse(
                resumes,
                new ResumeResponse.PaginationInfo(
                        page,
                        limit,
                        resumePage.getTotalElements(),
                        resumePage.getTotalPages()
                )
        );
    }

    public Resume getResume(String id) {
        return resumeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resume not found"));
    }

    public void deleteResume(String id) throws IOException {
        Resume resume = getResume(id);

        try {
            Path filePath = Paths.get(resume.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log but don't fail if file doesn't exist
        }

        resumeRepository.deleteById(id);
    }

    public MatchResponse matchJobs(String resumeId, int limit) {
        Resume resume = getResume(resumeId);

        List<JobMatcher.MatchResult> matches = jobMatcher.findMatchingJobs(resumeId, limit);

        if (!matches.isEmpty()) {
            List<Resume.MatchHistory> matchHistory = matches.stream()
                    .map(match -> {
                        Resume.MatchHistory history = new Resume.MatchHistory();
                        history.setJobId(match.getJob().getId());
                        history.setScore(match.getScore());
                        history.setMatchedKeywords(match.getMatchedKeywords());
                        return history;
                    })
                    .collect(Collectors.toList());

            List<Resume.MatchHistory> updatedHistory = new ArrayList<>(matchHistory);
            updatedHistory.addAll(resume.getMatchHistory());
            resume.setMatchHistory(updatedHistory.stream().limit(50).collect(Collectors.toList()));
            resumeRepository.save(resume);
        }

        List<MatchResponse.JobMatch> jobMatches = matches.stream()
                .map(match -> new MatchResponse.JobMatch(
                        new MatchResponse.JobInfo(
                                match.getJob().getId(),
                                match.getJob().getTitle(),
                                match.getJob().getCompany(),
                                match.getJob().getLocation(),
                                match.getJob().getJobType(),
                                match.getJob().getExperienceLevel(),
                                match.getJob().getPostedAt()
                        ),
                        match.getScore(),
                        match.getMatchedKeywords(),
                        new MatchResponse.ScoreBreakdown(
                                match.getBreakdown().getKeywords(),
                                match.getBreakdown().getSkills(),
                                match.getBreakdown().getTextSimilarity(),
                                match.getBreakdown().getExperienceLevel()
                        )
                ))
                .collect(Collectors.toList());

        return new MatchResponse(resumeId, jobMatches);
    }
}