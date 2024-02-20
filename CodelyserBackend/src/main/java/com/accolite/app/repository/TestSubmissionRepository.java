package com.accolite.app.repository;

import com.accolite.app.entity.TestSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestSubmissionRepository extends JpaRepository<TestSubmission,Long> {
    List<TestSubmission> findByCandidateId(Long candidateId);
}
