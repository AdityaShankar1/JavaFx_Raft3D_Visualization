package com.example.raftdemo.model;

/**
 * Model class representing a print job.
 * This is a simple POJO (Plain Old Java Object).
 */
public class PrintJob {

    private String id;       // unique job ID
    private String details;  // description of the job
    private String status;   // current status (SUBMITTED, IN_PROGRESS, DONE)

    // Getters and setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getDetails() {
        return details;
    }
    public void setDetails(String details) {
        this.details = details;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}