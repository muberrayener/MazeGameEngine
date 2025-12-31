package com.maze.util;

import com.maze.model.Maze;
import com.maze.model.Position;
import com.maze.model.Cell;

/**
 * Labirent validasyonu için yardımcı sınıf.
 */
public class MazeValidator {

    /**
     * Maze'in geçerli olup olmadığını kontrol eder
     * @param maze Labirent
     * @return true ise geçerli
     */
    public static boolean isValid(Maze maze) {
        if (maze == null) {
            return false;
        }

        if (maze.getRows() < 1 || maze.getCols() < 1) {
            return false;
        }

        if (maze.getGrid() == null) {
            return false;
        }

        return true;
    }

    /**
     * Start ve end pozisyonlarının geçerli olup olmadığını kontrol eder
     * @param maze Labirent
     * @param start Başlangıç
     * @param end Hedef
     * @return true ise geçerli
     */
    public static boolean arePositionsValid(Maze maze, Position start, Position end) {
        if (!isValid(maze) || start == null || end == null) {
            return false;
        }

        if (!maze.isValid(start) || !maze.isValid(end)) {
            return false;
        }

        Cell startCell = maze.getCell(start);
        Cell endCell = maze.getCell(end);

        if (startCell == null || endCell == null) {
            return false;
        }

        if (!startCell.isWalkable() || !endCell.isWalkable()) {
            return false;
        }

        return true;
    }

    /**
     * Maze'in çözülebilir olup olmadığını kontrol eder
     * @param maze Labirent
     * @param start Başlangıç
     * @param end Hedef
     * @return true ise çözülebilir
     */
    public static boolean isSolvable(Maze maze, Position start, Position end) {
        if (!arePositionsValid(maze, start, end)) {
            return false;
        }

        // Start ve end arasında path var mı kontrol et
        return GraphUtils.isConnected(maze, start);
    }

    /**
     * Position'ın maze sınırları içinde olup olmadığını kontrol eder
     * @param maze Labirent
     * @param pos Pozisyon
     * @return true ise sınırlar içinde
     */
    public static boolean isInBounds(Maze maze, Position pos) {
        if (maze == null || pos == null) {
            return false;
        }

        return maze.isValid(pos);
    }

    /**
     * Hücrenin walkable olup olmadığını kontrol eder
     * @param maze Labirent
     * @param pos Pozisyon
     * @return true ise walkable
     */
    public static boolean isWalkable(Maze maze, Position pos) {
        if (!isInBounds(maze, pos)) {
            return false;
        }

        Cell cell = maze.getCell(pos);
        return cell != null && cell.isWalkable();
    }

    /**
     * Maze'in en az bir çözümü olup olmadığını kontrol eder
     * @param maze Labirent
     * @return true ise çözülebilir
     */
    public static boolean hasAnySolution(Maze maze) {
        if (!isValid(maze)) {
            return false;
        }

        // En az 2 walkable hücre olmalı
        int walkableCount = GraphUtils.countWalkableCells(maze);
        if (walkableCount < 2) {
            return false;
        }

        // Start pozisyonu bul
        Position start = findFirstWalkablePosition(maze);
        if (start == null) {
            return false;
        }

        // Tüm walkable hücreler erişilebilir mi?
        return GraphUtils.isConnected(maze, start);
    }

    /**
     * İlk walkable pozisyonu bulur
     * @param maze Labirent
     * @return İlk walkable pozisyon (veya null)
     */
    private static Position findFirstWalkablePosition(Maze maze) {
        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                Cell cell = maze.getCell(r, c);
                if (cell != null && cell.isWalkable()) {
                    return cell.getPosition();
                }
            }
        }
        return null;
    }

    /**
     * Maze istatistiklerini string olarak döndürür
     * @param maze Labirent
     * @return İstatistik string
     */
    public static String getStatistics(Maze maze) {
        if (!isValid(maze)) {
            return "❌ No maze generated.";
        }

        int totalCells = maze.getRows() * maze.getCols();
        int walkableCells = GraphUtils.countWalkableCells(maze);
        int wallCells = totalCells - walkableCells;
        double walkableRatio = (double) walkableCells / totalCells * 100;

        return String.format(
                "\n📊 STATISTICS\n" +
                        "─────────────\n"+
                        "  Dimensions: %dx%d\n" +
                        "  Total Cells: %d\n" +
                        "  Walkable: %d (%.1f%%)\n" +
                        "  Walls: %d (%.1f%%)\n" +
                        "  Obstacles: %d",
                maze.getRows(), maze.getCols(),
                totalCells,
                walkableCells, walkableRatio,
                wallCells, 100 - walkableRatio,
                maze.getObstacles().size()
        );
    }
}
