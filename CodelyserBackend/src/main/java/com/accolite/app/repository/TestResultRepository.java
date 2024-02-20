package com.accolite.app.repository;

import com.accolite.app.entity.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestResultRepository extends JpaRepository<TestResult,Long> {
}
