package com.resume.repository;

import com.resume.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends MongoRepository<Job, String> {

    Page<Job> findByIsActive(Boolean isActive, Pageable pageable);

    Page<Job> findByIsActiveAndJobType(Boolean isActive, String jobType, Pageable pageable);

    Page<Job> findByIsActiveAndExperienceLevel(Boolean isActive, String experienceLevel, Pageable pageable);

    Page<Job> findByIsActiveAndJobTypeAndExperienceLevel(
            Boolean isActive, String jobType, String experienceLevel, Pageable pageable);

    @Query("{ 'isActive': ?0, 'location': { $regex: ?1, $options: 'i' } }")
    Page<Job> findByIsActiveAndLocationRegex(Boolean isActive, String location, Pageable pageable);

    @Query("{ 'isActive': ?0, 'company': { $regex: ?1, $options: 'i' } }")
    Page<Job> findByIsActiveAndCompanyRegex(Boolean isActive, String company, Pageable pageable);

    @Query("{ $text: { $search: ?0 }, 'isActive': true }")
    Page<Job> findByTextSearch(String search, Pageable pageable);

    long countByIsActive(Boolean isActive);

    List<Job> findTop50ByIsActiveOrderByPostedAtDesc(Boolean isActive);
}