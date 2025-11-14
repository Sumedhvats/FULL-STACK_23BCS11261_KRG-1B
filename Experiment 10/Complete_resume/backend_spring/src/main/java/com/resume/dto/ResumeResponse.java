package com.resume.dto;

import com.resume.model.Resume;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ResumeResponse {
    private List<Resume> resumes;
    private PaginationInfo pagination;

    @Data
    @AllArgsConstructor
    public static class PaginationInfo {
        private int page;
        private int limit;
        private long total;
        private int pages;
    }
}