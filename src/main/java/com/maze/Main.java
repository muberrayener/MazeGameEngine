package com.maze;

import com.maze.di.ServiceProvider;
import com.maze.service.GameEngine;
import com.maze.cli.CLIApplication;

/**
 * Ana entry point.
 * Uygulamayı başlatır.
 */

    public class Main {

        public static void main(String[] args) {
            if (args.length > 0 && args[0].equals("--gui")) {
                // Launch GUI properly
                javafx.application.Application.launch(com.maze.gui.MazeApplication.class, args);
            } else {
                runCLI();
            }
        }

        private static void runCLI() {
            ServiceProvider provider = new ServiceProvider();
            GameEngine gameEngine = provider.getGameEngine();
            CLIApplication cliApp = new CLIApplication(gameEngine);
            cliApp.start();
        }
    }

