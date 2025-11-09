package com.resume.dto;

import com.resume.model.Job;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class JobResponse {
    private List<Job> jobs;
    private PaginationInfo pagination;
    private Map<String, String> filters;

    @Data
    @AllArgsConstructor
    public static class PaginationInfo {
        private int page;
        private int limit;
        private long total;
        private int pages;
    }
}