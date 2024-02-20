package com.accolite.app.repository;

import com.accolite.app.entity.Question;
import com.accolite.app.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question,Long>{

}
