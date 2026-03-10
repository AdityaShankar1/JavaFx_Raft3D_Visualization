# JavaFx_Raft3D_Visualization

Visualization of Raft consensus algorithm for 3d printers using JavaFx (GUI), Spring Boot (REST API Endpoints GET/POST) and RabbitMQ (Message Broker) in Java 21.

## Overview

This project visualizes the Raft consensus algorithm using a 3-node cluster. Each node is a Spring Boot application that communicates with the others using RabbitMQ. The state of each node is visualized in real-time using a JavaFX 3D GUI.

Nodes transition through three states:

- 🔵 **FOLLOWER (Blue)**: The default state. Nodes wait for heartbeats from the leader.
- 🟠 **CANDIDATE (Orange)**: If a follower doesn't receive a heartbeat within its randomized election timeout (3-5s), it becomes a candidate and requests votes.
- 🟢 **LEADER (Green)**: The winner of the election. It sends periodic heartbeats (every 1s) to maintain authority. To simulate a realistic cluster, the leader automatically steps down after a randomized tenure (4-7s), forcing a new election sequence.

## Prerequisites

- **Java 21** (e.g., BellSoft Liberica JDK with JavaFX bundled)
- **RabbitMQ** running on `localhost:5672` (default guest/guest credentials)
- **Maven** (Wrapper included in project)

## Running the Project

The cluster consists of 3 Spring Boot nodes running on different ports (8080, 8081, 8082). You need to launch each node in a separate terminal window, and then launch the JavaFX GUI.

### 1. Start the RabbitMQ Broker

Ensure your RabbitMQ server is running on localhost.

### 2. Start the Raft Nodes

Open 3 separate terminals, navigate to the project root, and run:

**Terminal 1 (Node 1 - Port 8080):**

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=node1
```

**Terminal 2 (Node 2 - Port 8081):**

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=node2
```

**Terminal 3 (Node 3 - Port 8082):**

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=node3
```

### 3. Start the JavaFX Visualization

Open a 4th terminal and run the GUI:

```bash
./mvnw javafx:run
```

## Simulation Details

- The GUI updates dynamically every 2 seconds by polling the REST endpoints `/raft/state` of each node.
- The visualization runs for exactly **30 seconds**. After 30 seconds, the simulation halts, displays a `"Hope You Enjoyed Visualization of Raft"` alert popup, and gracefully shuts down.

## Architecture

- `RaftConsensusService.java`: Core Raft logic. Manages election timeouts, leader heartbeats, state transitions, and RabbitMQ message broadcasting/consumption.
- `Raft3DGui.java`: JavaFX application. Uses asynchronous `HttpClient` to poll node states without blocking the JavaFX Application Thread.
- `RaftMessageProducer.java` / `RaftMessageConsumer.java`: RabbitMQ integration for inter-node communication.
- `application.yaml`: Uses Spring Profiles (`node1`, `node2`, `node3`) to isolate ports and node IDs while sharing the same codebase.
