package com.maze.service;

import com.maze.core.*;
import com.maze.model.*;
import java.util.*;

/**
 * Ana oyun motoru - Facade Pattern.
 * Tüm bileşenleri koordine eder.
 *
 * Sorumluluklar:
 * - Labirent üretimi
 * - Yol bulma
 * - Oyun durumu yönetimi
 * - Event/callback yönetimi
 */
public class GameEngine {

    private final IMazeGenerator mazeGenerator;
    private final IPathFinder pathFinder;
    private final IObstacleManager obstacleManager;

    private GameState gameState;
    private Maze currentMaze;
    private Path currentPath;
    private List<IVisualizationListener> listeners;

    /**
     * Constructor Injection (DI)
     */
    public GameEngine(IMazeGenerator mazeGenerator,
                      IPathFinder pathFinder,
                      IObstacleManager obstacleManager) {
        this.mazeGenerator = mazeGenerator;
        this.pathFinder = pathFinder;
        this.obstacleManager = obstacleManager;
        this.gameState = new GameState();
        this.listeners = new ArrayList<>();
    }

    /**
     * Labirent üretir
     */
    public Maze generateMaze(int rows, int cols) {
        gameState.setCurrentState(GameState.State.GENERATING);
        notifyAlgorithmStarted(mazeGenerator.getAlgorithmName());

        long startTime = System.currentTimeMillis();

        try {
            currentMaze = mazeGenerator.generate(rows, cols);
            currentPath = null;
            gameState.setCurrentPath(null);
            gameState.setMaze(currentMaze);
            gameState.setCurrentState(GameState.State.GENERATED);

            long elapsedTime = System.currentTimeMillis() - startTime;
            gameState.setElapsedTime(elapsedTime);

            notifyAlgorithmCompleted(elapsedTime);

            return currentMaze;
        } catch (Exception e) {
            gameState.setCurrentState(GameState.State.IDLE);
            throw new RuntimeException("Maze generation failed", e);
        }
    }

    /**
     * Adım adım labirent üretir (animasyon için)
     */
    public List<AlgorithmStep> generateMazeWithAnimation(int rows, int cols) {
        gameState.setCurrentState(GameState.State.GENERATING);
        notifyAlgorithmStarted(mazeGenerator.getAlgorithmName());

        List<AlgorithmStep> steps = mazeGenerator.generateWithSteps(rows, cols);

        gameState.setCurrentState(GameState.State.GENERATED);
        notifyAlgorithmCompleted(0);

        return steps;
    }

    /**
     * Yol bulur
     */
    public Path findPath(Position start, Position end) {
        if (currentMaze == null) {
            throw new IllegalStateException("No maze generated");
        }

        gameState.setCurrentState(GameState.State.SOLVING);
        notifyAlgorithmStarted(pathFinder.getAlgorithmName());

        long startTime = System.currentTimeMillis();

        try {
            currentPath = pathFinder.findPath(currentMaze, start, end);

            if (currentPath.isEmpty()) {
                gameState.setCurrentState(GameState.State.NO_SOLUTION);
                notifyNoPathFound();
            } else {
                gameState.setCurrentState(GameState.State.SOLVED);
                gameState.setCurrentPath(currentPath);
                notifyPathFound(currentPath.getLength(), 0);
            }

            long elapsedTime = System.currentTimeMillis() - startTime;
            gameState.setElapsedTime(elapsedTime);

            notifyAlgorithmCompleted(elapsedTime);

            return currentPath;
        } catch (Exception e) {
            gameState.setCurrentState(GameState.State.IDLE);
            throw new RuntimeException("Path finding failed", e);
        }
    }

    /**
     * Adım adım yol bulur (animasyon için)
     */
    public List<AlgorithmStep> findPathWithAnimation(Position start, Position end) {
        if (currentMaze == null) {
            throw new IllegalStateException("No maze generated");
        }

        gameState.setCurrentState(GameState.State.SOLVING);
        notifyAlgorithmStarted(pathFinder.getAlgorithmName());

        List<AlgorithmStep> steps = pathFinder.findPathWithSteps(currentMaze, start, end);

        // Son adımdan path bilgisini al
        if (!steps.isEmpty()) {
            AlgorithmStep lastStep = steps.get(steps.size() - 1);
            if (lastStep.getType() == AlgorithmStep.StepType.COMPLETE
                    && !lastStep.getCurrentPath().isEmpty()) {
                currentPath = new Path(lastStep.getCurrentPath());
                gameState.setCurrentState(GameState.State.SOLVED);
                gameState.setCurrentPath(currentPath);
            } else {
                gameState.setCurrentState(GameState.State.NO_SOLUTION);
            }
        }

        notifyAlgorithmCompleted(0);

        return steps;
    }

    /**
     * Çoklu hedef için yol bulur
     */
    public Path findPathMultiTarget(Position start, List<Position> targets) {
        if (currentMaze == null) {
            throw new IllegalStateException("No maze generated");
        }

        gameState.setCurrentState(GameState.State.SOLVING);

        currentPath = pathFinder.findPathMultiTarget(currentMaze, start, targets);

        if (currentPath.isEmpty()) {
            gameState.setCurrentState(GameState.State.NO_SOLUTION);
        } else {
            gameState.setCurrentState(GameState.State.SOLVED);
            gameState.setCurrentPath(currentPath);
        }

        return currentPath;
    }

    /**
     * Dinamik engel ekler
     */
    public boolean addObstacle(Position position) {
        if (currentMaze == null) {
            return false;
        }

        return obstacleManager.addObstacle(currentMaze, position);
    }

    /**
     * Engeli kaldırır
     */
    public boolean removeObstacle(Position position) {
        if (currentMaze == null) {
            return false;
        }

        return obstacleManager.removeObstacle(currentMaze, position);
    }

    /**
     * Rastgele engeller ekler
     */
    public List<Position> addRandomObstacles(int count) {
        if (currentMaze == null) {
            return new ArrayList<>();
        }
        currentPath = null;
        gameState.setCurrentPath(null);
        gameState.setCurrentState(GameState.State.OBSTACLE_ADDED);
        return obstacleManager.addRandomObstacles(currentMaze, count);
    }

    /**
     * Tüm engelleri temizler
     */
    public void clearObstacles() {
        if (currentMaze != null) {
            obstacleManager.clearObstacles(currentMaze);
        }
    }

    /**
     * Oyunu sıfırlar
     */
    public void reset() {
        currentMaze = null;
        currentPath = null;
        gameState.reset();
    }

    /**
     * Listener ekler
     */
    public void addListener(IVisualizationListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Listener kaldırır
     */
    public void removeListener(IVisualizationListener listener) {
        listeners.remove(listener);
    }

    // Event notification methods
    private void notifyAlgorithmStarted(String algorithmName) {
        for (IVisualizationListener listener : listeners) {
            listener.onAlgorithmStarted(algorithmName);
        }
    }

    private void notifyAlgorithmCompleted(long elapsedTime) {
        for (IVisualizationListener listener : listeners) {
            listener.onAlgorithmCompleted(elapsedTime);
        }
    }

    private void notifyPathFound(int pathLength, int visitedCount) {
        for (IVisualizationListener listener : listeners) {
            listener.onPathFound(pathLength, visitedCount);
        }
    }

    private void notifyNoPathFound() {
        for (IVisualizationListener listener : listeners) {
            listener.onNoPathFound();
        }
    }

    // Getters
    public GameState getGameState() { return gameState; }
    public Maze getCurrentMaze() { return currentMaze; }
    public Path getCurrentPath() { return currentPath; }
    public IMazeGenerator getMazeGenerator() { return mazeGenerator; }
    public IPathFinder getPathFinder() { return pathFinder; }
    public IObstacleManager getObstacleManager() { return obstacleManager; }
}