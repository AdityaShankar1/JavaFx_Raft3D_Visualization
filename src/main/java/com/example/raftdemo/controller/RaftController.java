package com.example.raftdemo.controller;

import com.example.raftdemo.model.RaftNodeState;
import com.example.raftdemo.service.RaftConsensusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RaftController {

    private final RaftConsensusService consensusService;

    public RaftController(RaftConsensusService consensusService) {
        this.consensusService = consensusService;
    }

    @GetMapping(value = "/raft/state", produces = "text/plain")
    public String getState() {
        return consensusService.getState().name();
    }
}