package com.maze.gui;

import com.maze.di.ServiceProvider;
import com.maze.gui.controller.MainController;
import com.maze.service.GameEngine;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MazeApplication extends Application {

    private ServiceProvider provider;
    private GameEngine engine;

    @Override
    public void start(Stage primaryStage) {
        // Initialize DI container
        provider = new ServiceProvider();

        // Get engine from DI container
        engine = provider.getGameEngine(
                ServiceProvider.GeneratorType.PRIM,
                ServiceProvider.PathFinderType.ASTAR
        );

        // Create main controller with engine + DI
        MainController controller = new MainController(engine, provider);

        // Build Scene from controller's root
        Scene scene = new Scene(controller.getRoot());

        // Setup Stage
        primaryStage.setTitle("Maze Game Engine");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Launch JavaFX Application thread
        launch(args);
    }
}
