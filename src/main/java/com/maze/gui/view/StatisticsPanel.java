package com.maze.gui.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * İstatistik paneli - Sağ taraf.
 */
public class StatisticsPanel extends VBox {

    private Label dimensionsLabel;
    private Label walkableLabel;
    private Label wallsLabel;
    private Label obstaclesLabel;
    private Label pathLengthLabel;
    private Label pathTimeLabel;

    public StatisticsPanel() {
        setPadding(new Insets(10));
        setSpacing(15);
        setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1;");
        setPrefWidth(250);

        initializeStatistics();
    }

    private void initializeStatistics() {
        Label title = new Label("📊 Statistics");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Maze Stats
        TitledPane mazePane = createMazeStatsSection();

        // Path Stats
        TitledPane pathPane = createPathStatsSection();

        getChildren().addAll(title, mazePane, pathPane);
    }

    private TitledPane createMazeStatsSection() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(10));

        // Labels
        grid.add(new Label("Dimensions:"), 0, 0);
        dimensionsLabel = new Label("-");
        grid.add(dimensionsLabel, 1, 0);

        grid.add(new Label("Walkable:"), 0, 1);
        walkableLabel = new Label("-");
        grid.add(walkableLabel, 1, 1);

        grid.add(new Label("Walls:"), 0, 2);
        wallsLabel = new Label("-");
        grid.add(wallsLabel, 1, 2);

        grid.add(new Label("Obstacles:"), 0, 3);
        obstaclesLabel = new Label("-");
        grid.add(obstaclesLabel, 1, 3);

        TitledPane pane = new TitledPane("Maze Info", grid);
        pane.setExpanded(true);
        return pane;
    }

    private TitledPane createPathStatsSection() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(10));

        grid.add(new Label("Path Length:"), 0, 0);
        pathLengthLabel = new Label("-");
        grid.add(pathLengthLabel, 1, 0);

        grid.add(new Label("Time:"), 0, 1);
        pathTimeLabel = new Label("-");
        grid.add(pathTimeLabel, 1, 1);

        TitledPane pane = new TitledPane("Path Info", grid);
        pane.setExpanded(true);
        return pane;
    }

    public void setDimensions(int rows, int cols) {
        dimensionsLabel.setText(rows + " × " + cols);
    }

    public void setWalkable(int count) {
        walkableLabel.setText(String.valueOf(count));
    }

    public void setWalls(int count) {
        wallsLabel.setText(String.valueOf(count));
    }

    public void setObstacles(int count) {
        obstaclesLabel.setText(String.valueOf(count));
    }

    public void setPathLength(int length) {
        pathLengthLabel.setText(String.valueOf(length));
    }

    public void setPathTime(long ms) {
        pathTimeLabel.setText(ms + " ms");
    }

    public void clear() {
        dimensionsLabel.setText("-");
        walkableLabel.setText("-");
        wallsLabel.setText("-");
        obstaclesLabel.setText("-");
        pathLengthLabel.setText("-");
        pathTimeLabel.setText("-");
    }
}