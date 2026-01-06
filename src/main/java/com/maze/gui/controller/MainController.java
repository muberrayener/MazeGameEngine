package com.maze.gui.controller;

import com.maze.core.IVisualizationListener;
import com.maze.di.ServiceProvider;
import com.maze.model.*;
import com.maze.service.GameEngine;
import com.maze.gui.view.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Ana controller - MVC Pattern.
 */
public class MainController implements IVisualizationListener {

    private final GameEngine engine;
    private final ServiceProvider provider;

    private BorderPane root;
    private MazeCanvas mazeCanvas;
    private ControlPanel controlPanel;
    private StatusBar statusBar;
    private StatisticsPanel statsPanel;

    private Maze currentMaze;
    private Path currentPath;

    public MainController(GameEngine engine, ServiceProvider provider) {
        this.engine = engine;
        this.provider = provider;

        // Listener ekle
        engine.addListener(this);

        initializeUI();
    }

    private void initializeUI() {
        root = new BorderPane();
        root.setPadding(new Insets(10));

        // Center: Maze Canvas
        mazeCanvas = new MazeCanvas(600, 600);
        VBox centerBox = new VBox(10);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.getChildren().add(mazeCanvas);
        root.setCenter(centerBox);

        // Left: Control Panel
        controlPanel = new ControlPanel(this);
        root.setLeft(controlPanel);

        // Right: Statistics Panel
        statsPanel = new StatisticsPanel();
        root.setRight(statsPanel);

        // Bottom: Status Bar
        statusBar = new StatusBar();
        root.setBottom(statusBar);

        // Top: Menu Bar
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        // File Menu
        Menu fileMenu = new Menu("File");
        MenuItem newMaze = new MenuItem("New Maze");
        MenuItem exit = new MenuItem("Exit");

        newMaze.setOnAction(e -> handleGenerateMaze());
        exit.setOnAction(e -> Platform.exit());

        fileMenu.getItems().addAll(newMaze, new SeparatorMenuItem(), exit);

        // Algorithm Menu
        Menu algorithmMenu = new Menu("Algorithms");

        Menu generatorMenu = new Menu("Generator");
        MenuItem kruskal = new MenuItem("Kruskal");
        MenuItem prim = new MenuItem("Prim");
        MenuItem recursive = new MenuItem("Recursive Backtracker");

        kruskal.setOnAction(e -> changeGenerator(ServiceProvider.GeneratorType.KRUSKAL));
        prim.setOnAction(e -> changeGenerator(ServiceProvider.GeneratorType.PRIM));
        recursive.setOnAction(e -> changeGenerator(ServiceProvider.GeneratorType.RECURSIVE_BACKTRACKER));

        generatorMenu.getItems().addAll(kruskal, prim, recursive);

        Menu pathFinderMenu = new Menu("PathFinder");
        MenuItem bfs = new MenuItem("BFS");
        MenuItem dfs = new MenuItem("DFS");
        MenuItem astar = new MenuItem("A*");

        bfs.setOnAction(e -> changePathFinder(ServiceProvider.PathFinderType.BFS));
        dfs.setOnAction(e -> changePathFinder(ServiceProvider.PathFinderType.DFS));
        astar.setOnAction(e -> changePathFinder(ServiceProvider.PathFinderType.ASTAR));

        pathFinderMenu.getItems().addAll(bfs, dfs, astar);

        algorithmMenu.getItems().addAll(generatorMenu, pathFinderMenu);

        // Help Menu
        Menu helpMenu = new Menu("Help");
        MenuItem about = new MenuItem("About");
        about.setOnAction(e -> showAboutDialog());
        helpMenu.getItems().add(about);

        menuBar.getMenus().addAll(fileMenu, algorithmMenu, helpMenu);
        return menuBar;
    }

    // Public methods called from UI
    public void handleGenerateMaze() {
        int size = controlPanel.getMazeSize();

        statusBar.setStatus("Generating maze...");

        new Thread(() -> {
            try {
                currentMaze = engine.generateMaze(size, size);
                currentPath = null;

                Platform.runLater(() -> {
                    mazeCanvas.setMaze(currentMaze);
                    mazeCanvas.setPath(null);
                    updateStatistics();
                    statusBar.setStatus("Maze generated successfully!");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusBar.setStatus("Error: " + e.getMessage());
                    showErrorDialog("Generation Error", e.getMessage());
                });
            }
        }).start();
    }

    public void handleSolveMaze() {
        if (currentMaze == null) {
            showErrorDialog("No Maze", "Please generate a maze first!");
            return;
        }

        statusBar.setStatus("Solving maze...");

        new Thread(() -> {
            try {
                Position start = currentMaze.getStartPosition();
                Position end = currentMaze.getEndPosition();

                currentPath = engine.findPath(start, end);

                Platform.runLater(() -> {
                    mazeCanvas.setPath(currentPath);
                    updateStatistics();

                    if (currentPath.isEmpty()) {
                        statusBar.setStatus("No path found!");
                    } else {
                        statusBar.setStatus("Path found! Length: " + currentPath.getLength());
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusBar.setStatus("Error: " + e.getMessage());
                    showErrorDialog("Solving Error", e.getMessage());
                });
            }
        }).start();
    }

    public void handleAddObstacles() {
        if (currentMaze == null) {
            showErrorDialog("No Maze", "Please generate a maze first!");
            return;
        }

        int count = controlPanel.getObstacleCount();
        engine.addRandomObstacles(count);

        mazeCanvas.setMaze(currentMaze);
        updateStatistics();
        statusBar.setStatus("Added " + count + " obstacles");
    }

    public void handleClearObstacles() {
        if (currentMaze == null) return;

        engine.clearObstacles();
        mazeCanvas.setMaze(currentMaze);
        updateStatistics();
        statusBar.setStatus("Obstacles cleared");
    }

    public void handleReset() {
        currentMaze = null;
        currentPath = null;
        engine.reset();

        mazeCanvas.clear();
        updateStatistics();
        statusBar.setStatus("Reset complete");
    }

    private void changeGenerator(ServiceProvider.GeneratorType type) {
        // Engine'i yeni generator ile yeniden oluştur
        GameEngine newEngine = provider.getGameEngine(
                type,
                ServiceProvider.PathFinderType.ASTAR
        );

        // Listener'ı aktar
        newEngine.addListener(this);

        statusBar.setStatus("Generator changed to: " + type);
    }

    private void changePathFinder(ServiceProvider.PathFinderType type) {
        // Engine'i yeni pathfinder ile yeniden oluştur
        GameEngine newEngine = provider.getGameEngine(
                ServiceProvider.GeneratorType.KRUSKAL,
                type
        );

        newEngine.addListener(this);

        statusBar.setStatus("PathFinder changed to: " + type);
    }

    private void updateStatistics() {
        if (currentMaze == null) {
            statsPanel.clear();
            return;
        }

        int totalCells = currentMaze.getRows() * currentMaze.getCols();
        int walkable = 0;
        int walls = 0;

        for (int r = 0; r < currentMaze.getRows(); r++) {
            for (int c = 0; c < currentMaze.getCols(); c++) {
                if (currentMaze.getCell(r, c).isWalkable()) {
                    walkable++;
                } else {
                    walls++;
                }
            }
        }

        statsPanel.setDimensions(currentMaze.getRows(), currentMaze.getCols());
        statsPanel.setWalkable(walkable);
        statsPanel.setWalls(walls);
        statsPanel.setObstacles(currentMaze.getObstacles().size());

        if (currentPath != null && !currentPath.isEmpty()) {
            statsPanel.setPathLength(currentPath.getLength());
            statsPanel.setPathTime(currentPath.getComputationTimeMs());
        }
    }

    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Maze Game Engine v1.0");
        alert.setContentText(
                "Clean Architecture + Design Patterns\n\n" +
                        "Algorithms:\n" +
                        "• Generators: Kruskal, Prim, Recursive\n" +
                        "• PathFinders: BFS, DFS, A*\n\n" +
                        "Built with JavaFX & Dependency Injection"
        );
        alert.showAndWait();
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // IVisualizationListener implementation
    @Override
    public void onStepExecuted(AlgorithmStep step) {
        // Animation için kullanılabilir
    }

    @Override
    public void onCellVisited(Position position) {
        // Animation için kullanılabilir
    }

    @Override
    public void onCellExplored(Position position) {
        // Animation için kullanılabilir
    }

    @Override
    public void onPathFound(int pathLength, int visitedCount) {
        Platform.runLater(() -> {
            statusBar.setStatus("Path found! Length: " + pathLength);
        });
    }

    @Override
    public void onNoPathFound() {
        Platform.runLater(() -> {
            statusBar.setStatus("No path found!");
        });
    }

    @Override
    public void onAlgorithmStarted(String algorithmName) {
        Platform.runLater(() -> {
            statusBar.setStatus("Running: " + algorithmName);
        });
    }

    @Override
    public void onAlgorithmCompleted(long elapsedTimeMs) {
        Platform.runLater(() -> {
            statusBar.appendStatus(" (" + elapsedTimeMs + "ms)");
        });
    }

    public Parent getRoot() {
        return root;
    }

    public GameEngine getEngine() {
        return engine;
    }
}