package com.example.raftdemo.service;

import com.example.raftdemo.model.PrintJob;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service layer for managing print jobs.
 * Keeps jobs in memory for now (later you can replicate via Raft).
 */
@Service
public class JobService {

    // Simple in-memory store for jobs
    private final Map<String, PrintJob> jobs = new HashMap<>();

    /**
     * Add a new job to the system.
     */
    public PrintJob addJob(PrintJob job) {
        job.setStatus("SUBMITTED"); // default status
        jobs.put(job.getId(), job);
        return job;
    }

    /**
     * Retrieve a job by ID.
     */
    public PrintJob getJob(String id) {
        return jobs.getOrDefault(id, null);
    }

    /**
     * List all jobs.
     */
    public List<PrintJob> getAllJobs() {
        return new ArrayList<>(jobs.values());
    }
}