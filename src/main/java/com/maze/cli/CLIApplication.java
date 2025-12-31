package com.maze.cli;

import com.maze.service.GameEngine;
import com.maze.model.*;
import com.maze.util.MazeValidator;

import java.util.*;

/**
 * Command Line Interface uygulaması.
 * Kullanıcı ile interaktif etkileşim.
 */
public class CLIApplication {

    private final GameEngine engine;
    private final Scanner scanner;
    private Maze currentMaze;
    private boolean running;

    public CLIApplication(GameEngine engine) {
        this.engine = engine;
        this.scanner = new Scanner(System.in);
        this.running = true;
    }

    public void start() {
        printWelcome();

        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            handleCommand(choice);
        }

        scanner.close();
        System.out.println("\n👋 Goodbye!");
    }

    private void printWelcome() {
        System.out.println("Welcome to Maze Game Engine!");
        System.out.println("Type 'help' for available commands");
        System.out.println();
    }

    private void printMenu() {
        System.out.println("\n┌─────────────────────────────────┐");
        System.out.println("│  MAIN MENU                      │");
        System.out.println("├─────────────────────────────────┤");
        System.out.println("│  1. Generate Maze               │");
        System.out.println("│  2. Solve Maze                  │");
        System.out.println("│  3. Add Obstacles               │");
        System.out.println("│  4. View Maze                   │");
        System.out.println("│  5. Statistics                  │");
        System.out.println("│  6. Change Algorithm            │");
        System.out.println("│  7. Help                        │");
        System.out.println("│  0. Exit                        │");
        System.out.println("└─────────────────────────────────┘");
        System.out.print("Choose option: ");
    }

    private void handleCommand(String choice) {
        switch (choice) {
            case "1":
                generateMaze();
                break;
            case "2":
                solveMaze();
                break;
            case "3":
                addObstacles();
                break;
            case "4":
                viewMaze();
                break;
            case "5":
                showStatistics();
                break;
            case "6":
                changeAlgorithm();
                break;
            case "7":
            case "help":
                showHelp();
                break;
            case "0":
            case "exit":
            case "quit":
                running = false;
                break;
            default:
                System.out.println("❌ Invalid option. Try again.");
        }
    }

    private void generateMaze() {
        System.out.println("\n🎲 GENERATE MAZE");
        System.out.println("─────────────────");

        System.out.print("Enter maze size (e.g., 20 for 20x20): ");
        String input = scanner.nextLine().trim();

        try {
            int size = Integer.parseInt(input);

            if (size < 5 || size > 100) {
                System.out.println("❌ Size must be between 5 and 100");
                return;
            }

            System.out.println("\nGenerating maze...");
            long startTime = System.currentTimeMillis();

            currentMaze = engine.generateMaze(size, size);

            long elapsed = System.currentTimeMillis() - startTime;

            System.out.println("✓ Maze generated successfully!");
            System.out.println("  Size: " + size + "x" + size);
            System.out.println("  Algorithm: " + engine.getMazeGenerator().getAlgorithmName());
            System.out.println("  Time: " + elapsed + "ms");

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input. Please enter a number.");
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void solveMaze() {
        if (currentMaze == null) {
            System.out.println("❌ No maze generated. Please generate a maze first.");
            return;
        }

        System.out.println("\n🔍 SOLVE MAZE");
        System.out.println("─────────────");

        Position start = currentMaze.getStartPosition();
        Position end = currentMaze.getEndPosition();

        System.out.println("Finding path from " + start + " to " + end);
        System.out.println("Using: " + engine.getPathFinder().getAlgorithmName());

        Path path = engine.findPath(start, end);

        if (path.isEmpty()) {
            System.out.println("❌ No path found!");
        } else {
            System.out.println("✓ Path found!");
            System.out.println("  Length: " + path.getLength());
            System.out.println("  Cost: " + path.getCost());
            System.out.println("  Time: " + path.getComputationTimeMs() + "ms");
            System.out.println("  Optimal: " + engine.getPathFinder().isOptimal());
        }
    }

    private void addObstacles() {
        if (currentMaze == null) {
            System.out.println("❌ No maze generated.");
            return;
        }

        System.out.println("\n🚧 ADD OBSTACLES");
        System.out.println("────────────────");
        System.out.print("How many random obstacles? ");

        try {
            int count = Integer.parseInt(scanner.nextLine().trim());

            if (count < 1 || count > 50) {
                System.out.println("❌ Count must be between 1 and 50");
                return;
            }

            List<Position> added = engine.addRandomObstacles(count);
            System.out.println("✓ Added " + added.size() + " obstacles");

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input");
        }
    }

    private void viewMaze() {
        if (!MazeValidator.isValid(currentMaze)) {
            System.out.println("❌ No maze generated.");
            return;
        }

        System.out.println("\n👁️  VIEW MAZE");
        System.out.println("──────────────");

        Path currentPath = engine.getCurrentPath();
        Set<Position> pathPositions = new HashSet<>();

        if (currentPath != null) {
            pathPositions.addAll(currentPath.getPositions());
        }

        // Display maze (max 30x60 for console)
        int maxRows = Math.min(currentMaze.getRows(), 30);
        int maxCols = Math.min(currentMaze.getCols(), 60);

        for (int r = 0; r < maxRows; r++) {
            for (int c = 0; c < maxCols; c++) {
                Cell cell = currentMaze.getCell(r, c);
                Position pos = cell.getPosition();

                if (cell.getType() == Cell.Type.START) {
                    System.out.print("S ");
                } else if (cell.getType() == Cell.Type.END) {
                    System.out.print("E ");
                } else if (cell.getType() == Cell.Type.OBSTACLE) {
                    System.out.print("🚧");
                } else if (pathPositions.contains(pos)) {
                    System.out.print("··");
                } else if (cell.getType() == Cell.Type.WALL) {
                    System.out.print("██");
                } else {
                    System.out.print("  ");
                }
            }
            System.out.println();
        }

        if (currentMaze.getRows() > 30 || currentMaze.getCols() > 60) {
            System.out.println("\n(Maze truncated for display)");
        }

        System.out.println("\nLegend: S Start | E End | ·· Path | 🚧 Obstacle | ██ Wall");
    }

    private void showStatistics() {
        System.out.println(MazeValidator.getStatistics(currentMaze));
        GameState state = engine.getGameState();
        System.out.println("\nCurrent state: " + state.getCurrentState());

        if (state.getCurrentPath() != null) {
            System.out.println("Path length: " + state.getCurrentPath().getLength());
        }
    }

    private void changeAlgorithm() {
        System.out.println("\n⚙️  CHANGE ALGORITHM");
        System.out.println("────────────────────");
        System.out.println("This feature requires restart with different configuration.");
        System.out.println("Current algorithms:");
        System.out.println("  Generator: " + engine.getMazeGenerator().getAlgorithmName());
        System.out.println("  PathFinder: " + engine.getPathFinder().getAlgorithmName());
    }

    private void showHelp() {
        System.out.println("\n📖 HELP");
        System.out.println("────────");
        System.out.println("1. Generate Maze - Create a new random maze");
        System.out.println("2. Solve Maze - Find path from start to end");
        System.out.println("3. Add Obstacles - Add dynamic obstacles");
        System.out.println("4. View Maze - Display current maze");
        System.out.println("5. Statistics - Show maze statistics");
        System.out.println("6. Change Algorithm - View current algorithms");
        System.out.println("0. Exit - Quit the application");
        System.out.println("\nCommands: 'help', 'exit', 'quit'");
    }
}