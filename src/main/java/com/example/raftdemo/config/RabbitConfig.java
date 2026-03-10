package com.example.raftdemo.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration.
 * Defines a queue for Raft messages (AppendEntries, RequestVote).
 */
@Configuration
public class RabbitConfig {

    @Bean
    public Queue raftQueue() {
        return new Queue("raft-messages", false);
    }
}