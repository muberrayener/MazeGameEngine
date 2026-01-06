package com.maze.gui.view;

import com.maze.gui.controller.MainController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Kontrol paneli - Sol taraf.
 */
public class ControlPanel extends VBox {

    private final MainController controller;
    private Spinner<Integer> sizeSpinner;
    private Spinner<Integer> obstacleSpinner;

    public ControlPanel(MainController controller) {
        this.controller = controller;

        setPadding(new Insets(10));
        setSpacing(15);
        setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1;");
        setPrefWidth(250);

        initializeControls();
    }

    private void initializeControls() {
        // Title
        Label title = new Label("🎮 Controls");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Maze Generation Section
        TitledPane genPane = createGenerationSection();

        // Path Finding Section
        TitledPane pathPane = createPathFindingSection();

        // Obstacles Section
        TitledPane obstaclePane = createObstacleSection();

        // Utility Section
        TitledPane utilityPane = createUtilitySection();

        getChildren().addAll(title, genPane, pathPane, obstaclePane, utilityPane);
    }

    private TitledPane createGenerationSection() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        // Size spinner
        Label sizeLabel = new Label("Maze Size:");
        sizeSpinner = new Spinner<>(5, 100, 20, 5);
        sizeSpinner.setEditable(true);
        sizeSpinner.setPrefWidth(150);

        Button generateBtn = new Button("Generate Maze");
        generateBtn.setPrefWidth(200);
        generateBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        generateBtn.setOnAction(e -> controller.handleGenerateMaze());

        content.getChildren().addAll(sizeLabel, sizeSpinner, generateBtn);

        TitledPane pane = new TitledPane("1. Generation", content);
        pane.setExpanded(true);
        return pane;
    }

    private TitledPane createPathFindingSection() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Button solveBtn = new Button("Solve Maze");
        solveBtn.setPrefWidth(200);
        solveBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        solveBtn.setOnAction(e -> controller.handleSolveMaze());

        content.getChildren().addAll(solveBtn);

        TitledPane pane = new TitledPane("2. Path Finding", content);
        pane.setExpanded(true);
        return pane;
    }

    private TitledPane createObstacleSection() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Label obstacleLabel = new Label("Obstacle Count:");
        obstacleSpinner = new Spinner<>(1, 50, 5, 1);
        obstacleSpinner.setEditable(true);
        obstacleSpinner.setPrefWidth(150);

        Button addBtn = new Button("Add Obstacles");
        addBtn.setPrefWidth(200);
        addBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        addBtn.setOnAction(e -> controller.handleAddObstacles());

        Button clearBtn = new Button("Clear Obstacles");
        clearBtn.setPrefWidth(200);
        clearBtn.setOnAction(e -> controller.handleClearObstacles());

        content.getChildren().addAll(obstacleLabel, obstacleSpinner, addBtn, clearBtn);

        TitledPane pane = new TitledPane("3. Obstacles", content);
        pane.setExpanded(true);
        return pane;
    }

    private TitledPane createUtilitySection() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Button resetBtn = new Button("Reset All");
        resetBtn.setPrefWidth(200);
        resetBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        resetBtn.setOnAction(e -> controller.handleReset());

        content.getChildren().addAll(resetBtn);

        TitledPane pane = new TitledPane("4. Utility", content);
        pane.setExpanded(false);
        return pane;
    }

    public int getMazeSize() {
        return sizeSpinner.getValue();
    }

    public int getObstacleCount() {
        return obstacleSpinner.getValue();
    }
}