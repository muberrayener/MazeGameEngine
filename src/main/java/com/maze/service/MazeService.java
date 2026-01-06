package com.maze.service;

import com.maze.core.IMazeGenerator;
import com.maze.model.*;
import com.maze.util.*;
import java.util.*;

/**
 * Labirent işlemleri servisi.
 * Maze ile ilgili tüm operasyonlar burada.
 */
public class MazeService {

    private final IMazeGenerator generator;

    public MazeService(IMazeGenerator generator) {
        this.generator = generator;
    }

    /**
     * Labirent üretir ve validasyonu yapar
     */
    public Maze createValidMaze(int rows, int cols) {
        if (rows < 5 || cols < 5) {
            throw new IllegalArgumentException("Maze must be at least 5x5");
        }

        if (rows > 200 || cols > 200) {
            throw new IllegalArgumentException("Maze cannot exceed 200x200");
        }

        Maze maze = generator.generate(rows, cols);

        if (!MazeValidator.isValid(maze)) {
            throw new RuntimeException("Generated maze is invalid");
        }

        return maze;
    }

    /**
     * Labirent istatistiklerini döndürür
     */
    public MazeStatistics getStatistics(Maze maze) {
        if (!MazeValidator.isValid(maze)) {
            return null;
        }

        int totalCells = maze.getRows() * maze.getCols();
        int walkableCells = PathUtils.countWalkableCells(maze);
        int wallCells = totalCells - walkableCells;
        int obstacleCount = maze.getObstacles().size();

        double walkableRatio = (double) walkableCells / totalCells;
        boolean isConnected = walkableCells > 0 &&
                PathUtils.isConnected(maze, findFirstWalkable(maze));

        return new MazeStatistics(
                maze.getRows(),
                maze.getCols(),
                totalCells,
                walkableCells,
                wallCells,
                obstacleCount,
                walkableRatio,
                isConnected
        );
    }

    /**
     * Labirent için optimal start/end pozisyonları bulur
     */
    public PositionPair findOptimalStartEnd(Maze maze) {
        if (!MazeValidator.isValid(maze)) {
            return null;
        }

        // En uzak iki noktayı bul
        List<Position> walkablePositions = getAllWalkablePositions(maze);

        if (walkablePositions.size() < 2) {
            return null;
        }

        Position start = walkablePositions.get(0);
        Position end = walkablePositions.get(0);
        int maxDistance = 0;

        for (int i = 0; i < walkablePositions.size(); i++) {
            for (int j = i + 1; j < walkablePositions.size(); j++) {
                Position p1 = walkablePositions.get(i);
                Position p2 = walkablePositions.get(j);
                int distance = p1.manhattanDistance(p2);

                if (distance > maxDistance) {
                    maxDistance = distance;
                    start = p1;
                    end = p2;
                }
            }
        }

        return new PositionPair(start, end);
    }

    /**
     * Labirenti kopyalar
     */
    public Maze cloneMaze(Maze original) {
        if (original == null) {
            return null;
        }

        Maze clone = new Maze(original.getRows(), original.getCols());

        for (int r = 0; r < original.getRows(); r++) {
            for (int c = 0; c < original.getCols(); c++) {
                Cell originalCell = original.getCell(r, c);
                clone.setCell(r, c, originalCell.getType());
            }
        }

        clone.setStartPosition(original.getStartPosition());
        clone.setEndPosition(original.getEndPosition());

        for (Position obstacle : original.getObstacles()) {
            clone.addObstacle(obstacle);
        }

        return clone;
    }

    /**
     * Labirenti string'e dönüştürür (save için)
     */
    public String serializeMaze(Maze maze) {
        if (!MazeValidator.isValid(maze)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(maze.getRows()).append(",").append(maze.getCols()).append("\n");

        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                Cell cell = maze.getCell(r, c);
                sb.append(cellTypeToChar(cell.getType()));
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * String'den labirent oluşturur (load için)
     */
    public Maze deserializeMaze(String data) {
        String[] lines = data.split("\n");
        if (lines.length < 2) {
            return null;
        }

        String[] dimensions = lines[0].split(",");
        int rows = Integer.parseInt(dimensions[0]);
        int cols = Integer.parseInt(dimensions[1]);

        Maze maze = new Maze(rows, cols);

        for (int r = 0; r < rows && r + 1 < lines.length; r++) {
            String line = lines[r + 1];
            for (int c = 0; c < cols && c < line.length(); c++) {
                maze.setCell(r, c, charToCellType(line.charAt(c)));
            }
        }

        return maze;
    }

    // Helper methods
    private Position findFirstWalkable(Maze maze) {
        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                if (maze.getCell(r, c).isWalkable()) {
                    return new Position(r, c);
                }
            }
        }
        return null;
    }

    private List<Position> getAllWalkablePositions(Maze maze) {
        List<Position> positions = new ArrayList<>();
        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                if (maze.getCell(r, c).isWalkable()) {
                    positions.add(new Position(r, c));
                }
            }
        }
        return positions;
    }

    private char cellTypeToChar(Cell.Type type) {
        switch (type) {
            case WALL: return '#';
            case PATH: return ' ';
            case START: return 'S';
            case END: return 'E';
            case OBSTACLE: return 'X';
            default: return '?';
        }
    }

    private Cell.Type charToCellType(char c) {
        switch (c) {
            case '#': return Cell.Type.WALL;
            case ' ': return Cell.Type.PATH;
            case 'S': return Cell.Type.START;
            case 'E': return Cell.Type.END;
            case 'X': return Cell.Type.OBSTACLE;
            default: return Cell.Type.WALL;
        }
    }

    // Inner classes
    public static class MazeStatistics {
        public final int rows;
        public final int cols;
        public final int totalCells;
        public final int walkableCells;
        public final int wallCells;
        public final int obstacleCount;
        public final double walkableRatio;
        public final boolean isConnected;

        public MazeStatistics(int rows, int cols, int totalCells,
                              int walkableCells, int wallCells, int obstacleCount,
                              double walkableRatio, boolean isConnected) {
            this.rows = rows;
            this.cols = cols;
            this.totalCells = totalCells;
            this.walkableCells = walkableCells;
            this.wallCells = wallCells;
            this.obstacleCount = obstacleCount;
            this.walkableRatio = walkableRatio;
            this.isConnected = isConnected;
        }

        @Override
        public String toString() {
            return String.format(
                    "Maze %dx%d: %d walkable (%.1f%%), %d walls, %d obstacles, connected: %s",
                    rows, cols, walkableCells, walkableRatio * 100,
                    wallCells, obstacleCount, isConnected
            );
        }
    }

    public static class PositionPair {
        public final Position start;
        public final Position end;

        public PositionPair(Position start, Position end) {
            this.start = start;
            this.end = end;
        }
    }
}