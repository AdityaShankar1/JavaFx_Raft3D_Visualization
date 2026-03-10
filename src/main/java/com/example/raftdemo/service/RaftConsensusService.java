package com.example.raftdemo.service;

import com.example.raftdemo.model.RaftNodeState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class RaftConsensusService {

    private static final Logger log = LoggerFactory.getLogger(RaftConsensusService.class);

    private final RabbitTemplate rabbitTemplate;
    private final String nodeId;
    private final AtomicReference<RaftNodeState> state = new AtomicReference<>(RaftNodeState.FOLLOWER);

    private volatile long lastHeartbeat = System.currentTimeMillis();

    // Each node gets a randomised election timeout (5–8 s) so they don't all fire at once
    private final int electionTimeoutMs;

    // Leader sends a heartbeat every second
    private static final int HEARTBEAT_INTERVAL_MS = 1000;

    // Leader steps down after 4–7 s to simulate a node failure and force a new election
    private static final int LEADER_MIN_TENURE_MS = 4_000;
    private static final int LEADER_MAX_TENURE_MS = 7_000;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

    public RaftConsensusService(RabbitTemplate rabbitTemplate,
                                org.springframework.core.env.Environment env) {
        this.rabbitTemplate = rabbitTemplate;
        this.nodeId = env.getProperty("node.id", "unknown");

        this.electionTimeoutMs = 3000 + (int) (Math.random() * 2000); // 3–5 s
        log.info("Node {} starting. Election timeout = {} ms", nodeId, electionTimeoutMs);

        startElectionTimer();
        startHeartbeatSender();
    }

    public RaftNodeState getState() {
        return state.get();
    }

    public String getNodeId() {
        return nodeId;
    }

    // ── Election timer ────────────────────────────────────────────────────────

    private void startElectionTimer() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                long elapsed = System.currentTimeMillis() - lastHeartbeat;
                if (elapsed > electionTimeoutMs && state.get() == RaftNodeState.FOLLOWER) {
                    log.info("Node {} election timeout after {} ms. Becoming CANDIDATE…", nodeId, elapsed);
                    state.set(RaftNodeState.CANDIDATE);
                    startElection();
                }
            } catch (Exception e) {
                log.error("Election timer error", e);
            }
        }, 1000, 1000, TimeUnit.MILLISECONDS);
    }

    private void startElection() {
        log.info("Node {} entered CANDIDATE state, requesting votes...", nodeId);
        // Simulate waiting for votes before becoming Leader.
        // During this time it stays CANDIDATE and the GUI shows it in Orange.
        int voteWaitTime = 1000 + (int) (Math.random() * 500); // 1-1.5s delay
        
        scheduler.schedule(() -> {
            if (state.get() == RaftNodeState.CANDIDATE) {
                log.info("Node {} won election — now LEADER.", nodeId);
                state.set(RaftNodeState.LEADER);
                // Immediately broadcast so followers reset their timers
                sendHeartbeat();
                // Schedule automatic step-down to simulate leader failure → forces new election
                scheduleLeaderStepDown();
            }
        }, voteWaitTime, TimeUnit.MILLISECONDS);
    }

    /**
     * After a random tenure the leader voluntarily steps down to FOLLOWER.
     * It resets its own election timer (lastHeartbeat = now) so it doesn't
     * immediately re-elect itself — giving another node a chance to win.
     */
    private void scheduleLeaderStepDown() {
        int tenure = LEADER_MIN_TENURE_MS + (int) (Math.random() * (LEADER_MAX_TENURE_MS - LEADER_MIN_TENURE_MS));
        log.info("Node {} will step down from LEADER after {} ms.", nodeId, tenure);
        scheduler.schedule(() -> {
            if (state.get() == RaftNodeState.LEADER) {
                log.info("Node {} stepping down from LEADER (simulated failure).", nodeId);
                // Give itself a fresh timer so a *different* follower wins next round
                lastHeartbeat = System.currentTimeMillis();
                state.set(RaftNodeState.FOLLOWER);
                // Followers will time out after electionTimeoutMs and elect a new leader
            }
        }, tenure, TimeUnit.MILLISECONDS);
    }

    // ── Periodic heartbeat sender ─────────────────────────────────────────────

    private void startHeartbeatSender() {
        scheduler.scheduleAtFixedRate(() -> {
            if (state.get() == RaftNodeState.LEADER) {
                sendHeartbeat();
            }
        }, HEARTBEAT_INTERVAL_MS, HEARTBEAT_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    public void sendHeartbeat() {
        // Leader counts itself as "alive" — prevents it from triggering its own election timer
        lastHeartbeat = System.currentTimeMillis();
        String msg = "heartbeat from " + nodeId;
        try {
            rabbitTemplate.convertAndSend("raft-messages", msg);
            log.debug("Sent heartbeat: {}", msg);
        } catch (Exception e) {
            log.error("Failed to send heartbeat", e);
        }
    }

    // ── Heartbeat receiver ────────────────────────────────────────────────────

    public void receiveHeartbeat(String fromNode) {
        // Ignore our own messages (all nodes share one queue)
        if (nodeId.equals(fromNode)) {
            log.debug("Node {} ignoring its own heartbeat.", nodeId);
            return;
        }

        lastHeartbeat = System.currentTimeMillis();
        RaftNodeState current = state.get();
        if (current != RaftNodeState.FOLLOWER) {
            log.info("Node {} received heartbeat from {} — stepping down to FOLLOWER.", nodeId, fromNode);
            state.set(RaftNodeState.FOLLOWER);
        } else {
            log.debug("Node {} refreshed heartbeat timer (from {})", nodeId, fromNode);
        }
    }
}