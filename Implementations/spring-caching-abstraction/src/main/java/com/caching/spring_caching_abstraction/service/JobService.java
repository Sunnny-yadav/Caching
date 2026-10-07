package com.caching.spring_caching_abstraction.service;

import com.caching.spring_caching_abstraction.entity.Job;
import com.caching.spring_caching_abstraction.repository.JobDetailsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class JobService {

    @Autowired
    private JobDetailsRepo jobDetailsRepo;


    @Cacheable(value = "jobs", key = "#id", unless = "#result == null")
    public Job getJobById(Long id) {
        System.out.println("Executing the body of getJobById()");
        return jobDetailsRepo.findById(id).orElseThrow(() -> new RuntimeException("Job not found with id: " + id));
    }

    @CachePut(value = "jobs", key = "#jobDetails.id")
    public Job updateJob(Job jobDetails) {
        System.out.println("Executing the body of updateJob()");
        return jobDetailsRepo.save(jobDetails);
    }

    @CacheEvict(value = "jobs", key = "#id")
    public void deleteJob(Long id) {
        System.out.println("Executing the body of deleteJob()");
        jobDetailsRepo.deleteById(id);
    }

    /*
     * when a @Cacheable method is called from another method within the same class using "this",
     * the caching logic does not trigger — the call goes directly to the real method body.
     * The demostration is shown below, you will see that in console the method body execution msg will be displayed.
     * It means even if data is cached it won't be fetched from there.
     * */

    public List<Job> getAllJobs(List<Long> ids){
        List<Job> allJobs = new ArrayList<>();
        for(Long id :ids){
            allJobs.add(this.getJobById(id));
        }
        return allJobs;
    }


}
