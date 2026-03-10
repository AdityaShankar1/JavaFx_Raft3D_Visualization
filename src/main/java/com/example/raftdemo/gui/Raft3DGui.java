package com.example.raftdemo.gui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * JavaFX GUI to visualize Raft cluster with 3 nodes.
 * Each node is a circle whose color reflects its Raft state.
 *
 * FIX: HTTP calls are made asynchronously (sendAsync) so the
 * JavaFX Application Thread is never blocked. UI updates are
 * safely marshalled back onto the JAT via Platform.runLater().
 */
public class Raft3DGui extends Application {

    // One shared async client – does NOT block calling thread
    private final HttpClient client = HttpClient.newHttpClient();

    private final Circle node1 = new Circle(40, Color.GRAY);
    private final Circle node2 = new Circle(40, Color.GRAY);
    private final Circle node3 = new Circle(40, Color.GRAY);

    private final Text state1 = new Text("Node1: UNKNOWN");
    private final Text state2 = new Text("Node2: UNKNOWN");
    private final Text state3 = new Text("Node3: UNKNOWN");

    @Override
    public void start(Stage stage) {
        HBox root = new HBox(40,
                new VBox(10, node1, state1),
                new VBox(10, node2, state2),
                new VBox(10, node3, state3)
        );
        root.setStyle("-fx-alignment: center; -fx-padding: 30;");

        Scene scene = new Scene(root, 600, 300);
        stage.setTitle("Raft3D Cluster Visualization");
        stage.setScene(scene);
        stage.show();

        // Poll every 2 s — fast enough to catch transitions but not spammy
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
            refreshStateAsync("http://localhost:8080/raft/state", node1, state1, "Node1");
            refreshStateAsync("http://localhost:8081/raft/state", node2, state2, "Node2");
            refreshStateAsync("http://localhost:8082/raft/state", node3, state3, "Node3");
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        // Close after 30 seconds and show alert
        Timeline closeTimer = new Timeline(new KeyFrame(Duration.seconds(30), event -> {
            timeline.stop();
            Platform.runLater(() -> {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.INFORMATION,
                        "Hope You Enjoyed Visualization of Raft",
                        javafx.scene.control.ButtonType.CLOSE
                );
                alert.setTitle("Simulation Complete");
                alert.setHeaderText(null);
                alert.showAndWait();
                Platform.exit();
                System.exit(0);
            });
        }));
        closeTimer.play();

        // Also fire immediately so the UI is not blank for the first 2 s
        Platform.runLater(() -> {
            refreshStateAsync("http://localhost:8080/raft/state", node1, state1, "Node1");
            refreshStateAsync("http://localhost:8081/raft/state", node2, state2, "Node2");
            refreshStateAsync("http://localhost:8082/raft/state", node3, state3, "Node3");
        });
    }

    /**
     * Makes a non-blocking HTTP GET to {@code url} and updates the circle/label
     * on the JavaFX Application Thread once the response arrives.
     */
    private void refreshStateAsync(String url, Circle circle, Text label, String nodeName) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    // This callback runs on an IO thread – must not touch JavaFX nodes directly
                    String rawBody = response.body();
                    String raftState = rawBody.replace("\"", "").trim();

                    // Marshal the UI update onto the JavaFX Application Thread
                    Platform.runLater(() -> {
                        label.setText(nodeName + ": " + raftState);
                        switch (raftState) {
                            case "FOLLOWER"  -> circle.setFill(Color.BLUE);
                            case "CANDIDATE" -> circle.setFill(Color.ORANGE);
                            case "LEADER"    -> circle.setFill(Color.GREEN);
                            default          -> circle.setFill(Color.GRAY);
                        }
                    });
                })
                .exceptionally(ex -> {
                    // Server not yet ready or unreachable
                    Platform.runLater(() -> {
                        label.setText(nodeName + ": OFFLINE");
                        circle.setFill(Color.RED);
                    });
                    return null;
                });
    }

    public static void main(String[] args) {
        launch();
    }
}