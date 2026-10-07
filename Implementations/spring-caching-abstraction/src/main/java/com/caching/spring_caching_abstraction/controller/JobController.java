package com.caching.spring_caching_abstraction.controller;

import com.caching.spring_caching_abstraction.entity.Job;
import com.caching.spring_caching_abstraction.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job")
public class JobController {

    @Autowired
    private JobService jobService;

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable("id") Long id){
        Job response = jobService.getJobById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Job> updateJob(@PathVariable("id") Long id,  @RequestBody Job jobDetails){
        jobDetails.setId(id);
        Job response = jobService.updateJob(jobDetails);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteJob(@PathVariable("id") Long id){
        jobService.deleteJob(id);
        return "Deletion succesful";
    }

    @GetMapping("/getAllJobs/{id1}/{id2}")
    public ResponseEntity<List<Job>> getAllJobs(@PathVariable("id1") Long id1, @PathVariable("id2") Long id2){
        List<Job> jobs = jobService.getAllJobs(List.of(id1, id2));
        return ResponseEntity.status(HttpStatus.OK).body(jobs);
    }

}
