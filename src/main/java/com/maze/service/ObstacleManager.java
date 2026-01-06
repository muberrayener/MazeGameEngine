package com.maze.service;

import com.maze.core.IObstacleManager;
import com.maze.model.*;
import com.maze.util.*;
import java.util.*;

/**
 * Dinamik engel yönetimi.
 * Runtime'da engel ekleme/çıkarma.
 */
public class ObstacleManager implements IObstacleManager {

    private final Random random = new Random();

    @Override
    public boolean addObstacle(Maze maze, Position position) {
        if (!MazeValidator.isInBounds(maze, position)) {
            return false;
        }

        Cell cell = maze.getCell(position);

        // Start veya End'e engel eklenemez
        if (cell.getType() == Cell.Type.START || cell.getType() == Cell.Type.END) {
            return false;
        }

        // Zaten engel varsa
        if (cell.getType() == Cell.Type.OBSTACLE) {
            return false;
        }

        // Engeli ekle
        maze.addObstacle(position);
        return true;
    }

    @Override
    public boolean removeObstacle(Maze maze, Position position) {
        if (!MazeValidator.isInBounds(maze, position)) {
            return false;
        }

        Cell cell = maze.getCell(position);

        if (cell.getType() != Cell.Type.OBSTACLE) {
            return false;
        }

        maze.removeObstacle(position);
        return true;
    }

    @Override
    public void clearObstacles(Maze maze) {
        if (maze == null) {
            return;
        }

        List<Position> obstacles = new ArrayList<>(maze.getObstacles());
        for (Position obstacle : obstacles) {
            maze.removeObstacle(obstacle);
        }
    }

    @Override
    public List<Position> addRandomObstacles(Maze maze, int count) {
        if (maze == null || count <= 0) {
            return new ArrayList<>();
        }

        List<Position> addedObstacles = new ArrayList<>();
        int attempts = 0;
        int maxAttempts = count * 10;

        while (addedObstacles.size() < count && attempts < maxAttempts) {
            Position pos = PathUtils.getRandomWalkablePosition(maze);

            if (pos != null && addObstacle(maze, pos)) {
                addedObstacles.add(pos);
            }

            attempts++;
        }

        return addedObstacles;
    }

    @Override
    public List<Position> getObstacles(Maze maze) {
        if (maze == null) {
            return new ArrayList<>();
        }

        return maze.getObstacles();
    }

    @Override
    public boolean isObstacle(Maze maze, Position position) {
        if (!MazeValidator.isInBounds(maze, position)) {
            return false;
        }

        Cell cell = maze.getCell(position);
        return cell.getType() == Cell.Type.OBSTACLE;
    }

    @Override
    public int getObstacleCount(Maze maze) {
        if (maze == null) {
            return 0;
        }

        return maze.getObstacles().size();
    }

    /**
     * Engellerin path'i bloke edip etmediğini kontrol eder
     */
    public boolean isPathBlocked(Maze maze, Position start, Position end) {
        return !PathUtils.isConnected(maze, start);
    }

    /**
     * Path'i tamamen bloke edecek engeller ekler (challenge mode)
     */
    public boolean createObstacleChallenge(Maze maze, Path originalPath, int obstacleCount) {
        if (originalPath == null || originalPath.isEmpty()) {
            return false;
        }

        List<Position> pathPositions = originalPath.getPositions();

        // Path üzerinde olmayan pozisyonlara engel ekle
        List<Position> candidates = new ArrayList<>();
        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                Position pos = new Position(r, c);
                if (maze.getCell(pos).isWalkable() && !pathPositions.contains(pos)) {
                    candidates.add(pos);
                }
            }
        }

        Collections.shuffle(candidates, random);

        int added = 0;
        for (Position pos : candidates) {
            if (added >= obstacleCount) break;
            if (addObstacle(maze, pos)) {
                added++;
            }
        }

        return added > 0;
    }
}