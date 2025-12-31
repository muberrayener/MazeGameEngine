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
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║   🎮 MAZE GAME ENGINE v1.0           ║");
        System.out.println("║   Clean Architecture + DI Pattern    ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.println();

        try {
            // DI Container ile servisleri başlat
            ServiceProvider provider = new ServiceProvider();

            // GameEngine'i DI ile al
            GameEngine gameEngine = provider.getGameEngine();

            System.out.println("✓ Services initialized successfully");
            System.out.println("✓ DI Container configured");
            System.out.println("✓ Game Engine ready");
            System.out.println();

            // CLI uygulamasını başlat
            if (args.length > 0 && args[0].equals("--demo")) {
                runDemo(provider);
            } else {
                CLIApplication cliApp = new CLIApplication(gameEngine);
                cliApp.start();
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to start application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Demo modu - Otomatik çalışır
     */
    private static void runDemo(ServiceProvider provider) {
        System.out.println("🎬 Running Demo Mode...");
        System.out.println();

        // Kruskal + A* ile demo
        System.out.println("Demo 1: Kruskal Generator + A* PathFinder");
        System.out.println("─────────────────────────────────────────");
        GameEngine engine1 = provider.getGameEngine(
                ServiceProvider.GeneratorType.KRUSKAL,
                ServiceProvider.PathFinderType.ASTAR
        );
        runSingleDemo(engine1, 15, 15);

        System.out.println();

        // Prim + BFS ile demo
        System.out.println("Demo 2: Prim Generator + BFS PathFinder");
        System.out.println("─────────────────────────────────────────");
        GameEngine engine2 = provider.getGameEngine(
                ServiceProvider.GeneratorType.PRIM,
                ServiceProvider.PathFinderType.BFS
        );
        runSingleDemo(engine2, 15, 15);

        System.out.println();

        // Recursive + DFS ile demo
        System.out.println("Demo 3: Recursive Backtracker + DFS PathFinder");
        System.out.println("─────────────────────────────────────────────");
        GameEngine engine3 = provider.getGameEngine(
                ServiceProvider.GeneratorType.RECURSIVE_BACKTRACKER,
                ServiceProvider.PathFinderType.DFS
        );
        runSingleDemo(engine3, 15, 15);
    }

    private static void runSingleDemo(GameEngine engine, int rows, int cols) {
        // Maze üret
        var maze = engine.generateMaze(rows, cols);
        System.out.println("✓ Maze generated: " + rows + "x" + cols);
        System.out.println("  Algorithm: " + engine.getMazeGenerator().getAlgorithmName());
        System.out.println("  Complexity: " + engine.getMazeGenerator().getTimeComplexity());

        // Yol bul
        var path = engine.findPath(
                maze.getStartPosition(),
                maze.getEndPosition()
        );

        if (!path.isEmpty()) {
            System.out.println("✓ Path found!");
            System.out.println("  Algorithm: " + engine.getPathFinder().getAlgorithmName());
            System.out.println("  Path length: " + path.getLength());
            System.out.println("  Computation time: " + path.getComputationTimeMs() + "ms");
            System.out.println("  Optimal: " + engine.getPathFinder().isOptimal());
        } else {
            System.out.println("✗ No path found");
        }

        // Basit görselleştirme
        System.out.println("\nMaze visualization:");
        printMaze(maze, path);
    }

    private static void printMaze(com.maze.model.Maze maze, com.maze.model.Path path) {
        var pathPositions = path.getPositions();

        for (int r = 0; r < Math.min(maze.getRows(), 20); r++) {
            for (int c = 0; c < Math.min(maze.getCols(), 40); c++) {
                var cell = maze.getCell(r, c);
                var pos = cell.getPosition();

                if (cell.getType() == com.maze.model.Cell.Type.START) {
                    System.out.print("S");
                } else if (cell.getType() == com.maze.model.Cell.Type.END) {
                    System.out.print("E");
                } else if (pathPositions.contains(pos)) {
                    System.out.print("·");
                } else if (cell.getType() == com.maze.model.Cell.Type.WALL) {
                    System.out.print("█");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }

        if (maze.getRows() > 20 || maze.getCols() > 40) {
            System.out.println("(Maze truncated for display)");
        }
    }
}