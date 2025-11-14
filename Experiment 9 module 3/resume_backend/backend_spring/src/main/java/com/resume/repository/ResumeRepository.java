package com.resume.repository;

import com.resume.model.Resume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeRepository extends MongoRepository<Resume, String> {

    Page<Resume> findAllByOrderByUploadedAtDesc(Pageable pageable);
}