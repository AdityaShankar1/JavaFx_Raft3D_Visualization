package com.example.raftdemo.raft;

import com.example.raftdemo.service.RaftConsensusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RaftMessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(RaftMessageConsumer.class);

    private final RaftConsensusService consensusService;

    public RaftMessageConsumer(RaftConsensusService consensusService) {
        this.consensusService = consensusService;
    }

    @RabbitListener(queues = "raft-messages")
    public void handleMessage(String message) {
        log.info("Received message: {}", message);
        // Extract sender node ID
        String fromNode = message.replace("heartbeat from ", "").trim();
        consensusService.receiveHeartbeat(fromNode);
    }
}