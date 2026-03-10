package com.example.raftdemo.model;

/**
 * Enum representing the state of a Raft node.
 */
public enum RaftNodeState {
    FOLLOWER,
    CANDIDATE,
    LEADER
}