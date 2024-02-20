package com.accolite.app.repository;

import com.accolite.app.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedBackServiceRepository extends JpaRepository<Feedback,Long>{

}
