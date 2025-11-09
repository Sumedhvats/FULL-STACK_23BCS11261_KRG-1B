package com.resume.util;

import com.resume.model.Job;
import com.resume.model.Resume;
import com.resume.repository.JobRepository;
import com.resume.repository.ResumeRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JobMatcher {

    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;

    private static final Map<String, Double> WEIGHTS = Map.of(
            "keywords", 0.4,
            "skills", 0.3,
            "textSimilarity", 0.2,
            "experienceLevel", 0.1
    );

    public List<MatchResult> findMatchingJobs(String resumeId, int limit) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        List<Job> activeJobs = jobRepository.findTop50ByIsActiveOrderByPostedAtDesc(true);

        List<MatchResult> matches = activeJobs.stream()
                .map(job -> {
                    ScoreResult scoreResult = calculateMatchScore(resume, job);
                    MatchResult match = new MatchResult();
                    match.setJob(job);
                    match.setScore(scoreResult.getScore());
                    match.setMatchedKeywords(scoreResult.getMatchedKeywords());
                    match.setBreakdown(scoreResult.getBreakdown());
                    return match;
                })
                .sorted(Comparator.comparingDouble(MatchResult::getScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());

        return matches;
    }

    private ScoreResult calculateMatchScore(Resume resume, Job job) {
        double score = 0;
        double totalPossibleScore = 0;
        List<String> matchedKeywords = new ArrayList<>();

        double keywordScore = calculateKeywordMatch(
                resume.getKeywords() != null ? resume.getKeywords() : new ArrayList<>(),
                job.getKeywords() != null ? job.getKeywords() : new ArrayList<>(),
                matchedKeywords
        );
        score += keywordScore * WEIGHTS.get("keywords");
        totalPossibleScore += 100 * WEIGHTS.get("keywords");

        List<String> allJobSkills = new ArrayList<>();
        if (job.getRequiredSkills() != null) allJobSkills.addAll(job.getRequiredSkills());
        if (job.getPreferredSkills() != null) allJobSkills.addAll(job.getPreferredSkills());

        double skillsScore = calculateSkillsMatch(
                resume.getSkills() != null ? resume.getSkills() : new ArrayList<>(),
                allJobSkills,
                matchedKeywords
        );
        score += skillsScore * WEIGHTS.get("skills");
        totalPossibleScore += 100 * WEIGHTS.get("skills");

        String jobText = (job.getDescription() != null ? job.getDescription() : "") + " " +
                (job.getRequirements() != null ? job.getRequirements() : "");
        double textScore = calculateTextSimilarity(
                resume.getExtractedText() != null ? resume.getExtractedText() : "",
                jobText
        );
        score += textScore * WEIGHTS.get("textSimilarity");
        totalPossibleScore += 100 * WEIGHTS.get("textSimilarity");

        double expScore = calculateExperienceMatch(resume, job);
        score += expScore * WEIGHTS.get("experienceLevel");
        totalPossibleScore += 100 * WEIGHTS.get("experienceLevel");

        double finalScore = totalPossibleScore > 0 ? (score / totalPossibleScore) * 100 : 0;

        ScoreResult result = new ScoreResult();
        result.setScore(Math.round(finalScore * 100.0) / 100.0);
        result.setMatchedKeywords(new ArrayList<>(new HashSet<>(matchedKeywords)));

        Breakdown breakdown = new Breakdown();
        breakdown.setKeywords(Math.round(keywordScore * 100.0) / 100.0);
        breakdown.setSkills(Math.round(skillsScore * 100.0) / 100.0);
        breakdown.setTextSimilarity(Math.round(textScore * 100.0) / 100.0);
        breakdown.setExperienceLevel(Math.round(expScore * 100.0) / 100.0);
        result.setBreakdown(breakdown);

        return result;
    }

    private double calculateKeywordMatch(List<String> resumeKeywords, List<String> jobKeywords,
                                         List<String> matchedKeywords) {
        if (jobKeywords.isEmpty()) return 0;

        long matches = jobKeywords.stream()
                .filter(jobKeyword -> resumeKeywords.stream().anyMatch(resumeKeyword ->
                        resumeKeyword.toLowerCase().contains(jobKeyword.toLowerCase()) ||
                                jobKeyword.toLowerCase().contains(resumeKeyword.toLowerCase())
                ))
                .peek(matchedKeywords::add)
                .count();

        return ((double) matches / jobKeywords.size()) * 100;
    }

    private double calculateSkillsMatch(List<String> resumeSkills, List<String> jobSkills,
                                        List<String> matchedKeywords) {
        if (jobSkills.isEmpty()) return 0;

        long matches = jobSkills.stream()
                .filter(jobSkill -> resumeSkills.stream().anyMatch(resumeSkill ->
                        resumeSkill.toLowerCase().contains(jobSkill.toLowerCase()) ||
                                jobSkill.toLowerCase().contains(resumeSkill.toLowerCase())
                ))
                .peek(matchedKeywords::add)
                .count();

        return ((double) matches / jobSkills.size()) * 100;
    }

    private double calculateTextSimilarity(String resumeText, String jobText) {
        Set<String> resumeWords = Arrays.stream(resumeText.toLowerCase().split("\\W+"))
                .filter(w -> w.length() > 3)
                .collect(Collectors.toSet());

        Set<String> jobWords = Arrays.stream(jobText.toLowerCase().split("\\W+"))
                .filter(w -> w.length() > 3)
                .collect(Collectors.toSet());

        Set<String> intersection = new HashSet<>(resumeWords);
        intersection.retainAll(jobWords);

        Set<String> union = new HashSet<>(resumeWords);
        union.addAll(jobWords);

        return union.size() > 0 ? ((double) intersection.size() / union.size()) * 100 : 0;
    }

    private double calculateExperienceMatch(Resume resume, Job job) {
        String experienceText = resume.getExperience() != null ?
                resume.getExperience().toLowerCase() : "";

        Pattern yearsPattern = Pattern.compile("(\\d+)\\s*years?");
        Matcher matcher = yearsPattern.matcher(experienceText);

        if (!matcher.find()) return 50;

        int years = Integer.parseInt(matcher.group(1));
        String jobLevel = job.getExperienceLevel() != null ? job.getExperienceLevel() : "mid";

        Map<String, int[]> levelRanges = Map.of(
                "entry", new int[]{0, 2},
                "mid", new int[]{2, 5},
                "senior", new int[]{5, 10},
                "lead", new int[]{8, 15},
                "executive", new int[]{10, 30}
        );

        int[] range = levelRanges.getOrDefault(jobLevel, new int[]{0, 30});
        int min = range[0];
        int max = range[1];

        if (years >= min && years <= max) return 100;
        if (years < min) return Math.max(0, 100 - (min - years) * 20);
        if (years > max) return Math.max(0, 100 - (years - max) * 10);

        return 50;
    }

    @Data
    public static class MatchResult {
        private Job job;
        private double score;
        private List<String> matchedKeywords;
        private Breakdown breakdown;
    }

    @Data
    private static class ScoreResult {
        private double score;
        private List<String> matchedKeywords;
        private Breakdown breakdown;
    }

    @Data
    public static class Breakdown {
        private double keywords;
        private double skills;
        private double textSimilarity;
        private double experienceLevel;
    }
}