package com.accolite.app.repository;

import com.accolite.app.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase,Long> {
    List<TestCase> findAllByQuestionId(Long questionId);
    boolean existsByQuestionId(Long questionId);
}