package com.example.raftdemo.model;

/**
 * Represents a single log entry in the Raft consensus protocol.
 * Each entry corresponds to a client command (e.g., submit job).
 */
public class RaftLogEntry {

    private int term;       // term when entry was received
    private String command; // command to apply (e.g., "ADD_JOB job1")

    public RaftLogEntry(int term, String command) {
        this.term = term;
        this.command = command;
    }

    public int getTerm() {
        return term;
    }
    public String getCommand() {
        return command;
    }
}