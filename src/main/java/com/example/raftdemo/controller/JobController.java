package com.example.raftdemo.controller;

import com.example.raftdemo.model.PrintJob;
import com.example.raftdemo.service.JobService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for handling print job requests.
 * Exposes endpoints for submitting jobs and checking job status.
 */
@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;

    // Constructor injection (preferred in Spring)
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    /**
     * Submit a new print job.
     * Example: POST /jobs with JSON body {"id":"job1","details":"3D print part"}
     */
    @PostMapping
    public PrintJob submitJob(@RequestBody PrintJob job) {
        return jobService.addJob(job);
    }

    /**
     * Get the status of a specific job by ID.
     * Example: GET /jobs/job1
     */
    @GetMapping("/{id}")
    public PrintJob getJob(@PathVariable String id) {
        return jobService.getJob(id);
    }

    /**
     * List all jobs currently tracked.
     * Example: GET /jobs
     */
    @GetMapping
    public List<PrintJob> listJobs() {
        return jobService.getAllJobs();
    }
}