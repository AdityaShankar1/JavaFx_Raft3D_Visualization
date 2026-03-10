package com.example.raftdemo.model;

/**
 * Model class representing a 3D printer.
 */
public class Printer {

    private String id;     // unique printer ID
    private String specs;  // printer specifications (e.g., model, capabilities)

    // Getters and setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getSpecs() {
        return specs;
    }
    public void setSpecs(String specs) {
        this.specs = specs;
    }
}