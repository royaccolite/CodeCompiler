package com.accolite.app.repository;


import com.accolite.app.entity.TestCaseOutput;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestCaseOutputRepository extends JpaRepository<TestCaseOutput,Long> {
}
