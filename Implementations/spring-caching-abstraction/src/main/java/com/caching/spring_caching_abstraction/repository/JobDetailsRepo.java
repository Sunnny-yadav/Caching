package com.caching.spring_caching_abstraction.repository;

import com.caching.spring_caching_abstraction.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobDetailsRepo extends JpaRepository<Job,Long> {
}
