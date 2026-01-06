package com.maze.gui;

import com.maze.di.ServiceProvider;
import com.maze.gui.controller.MainController;
import com.maze.service.GameEngine;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX GUI uygulaması.
 * Ana entry point.
 */
public class MazeApplication extends Application {

    private ServiceProvider serviceProvider;
    private GameEngine gameEngine;

    @Override
    public void init() throws Exception {
        // DI Container'ı başlat
        serviceProvider = new ServiceProvider();
        gameEngine = serviceProvider.getGameEngine();

        System.out.println("✓ JavaFX Application initialized");
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("🎮 Maze Game Engine");

        // MainController oluştur
        MainController controller = new MainController(gameEngine, serviceProvider);

        // Scene oluştur
        Scene scene = new Scene(controller.getRoot(), 1200, 800);

        // CSS ekle (opsiyonel)
        scene.getStylesheets().add(
                getClass().getResource("/styles/dark-theme.css").toExternalForm()
        );

        primaryStage.setScene(scene);
        primaryStage.show();

        System.out.println("✓ GUI started successfully");
    }

    @Override
    public void stop() {
        System.out.println("👋 Application closing...");
    }

    public static void main(String[] args) {
        launch(args);
    }
}