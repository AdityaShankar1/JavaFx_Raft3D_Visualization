package com.example.raftdemo.raft;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Producer for Raft messages.
 * Sends messages to the "raft-messages" queue.
 * Falls back to local logging if RabbitMQ is unavailable.
 */
@Component
public class RaftMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public RaftMessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(String message) {
        try {
            rabbitTemplate.convertAndSend("raft-messages", message);
            System.out.println("Sent Raft message: " + message);
        } catch (Exception e) {
            // Fallback: log locally instead of crashing
            System.err.println("RabbitMQ unavailable. Fallback: storing message locally -> " + message);
        }
    }
}