package com.maze.util;

import com.maze.model.*;
import java.util.*;

/**
 * Yol işlemleri için yardımcı metotlar.
 */
public class PathUtils {

    /**
     * Path'in geçerli olup olmadığını kontrol eder
     * @param path Yol
     * @param maze Labirent
     * @return true ise geçerli
     */
    public static boolean isValidPath(Path path, Maze maze) {
        if (path == null || path.isEmpty() || maze == null) {
            return false;
        }

        List<Position> positions = path.getPositions();

        // Tüm pozisyonlar walkable olmalı
        for (Position pos : positions) {
            if (!MazeValidator.isWalkable(maze, pos)) {
                return false;
            }
        }

        // Ardışık pozisyonlar komşu olmalı
        for (int i = 0; i < positions.size() - 1; i++) {
            Position current = positions.get(i);
            Position next = positions.get(i + 1);

            if (!areNeighbors(current, next)) {
                return false;
            }
        }

        return true;
    }

    /**
     * İki pozisyon komşu mu kontrol eder
     * @param a İlk pozisyon
     * @param b İkinci pozisyon
     * @return true ise komşu
     */
    public static boolean areNeighbors(Position a, Position b) {
        int rowDiff = Math.abs(a.getRow() - b.getRow());
        int colDiff = Math.abs(a.getCol() - b.getCol());

        // Manhattan distance = 1 (4 yönlü komşu)
        return (rowDiff == 1 && colDiff == 0) ||
                (rowDiff == 0 && colDiff == 1);
    }

    /**
     * Path'in uzunluğunu hesaplar
     * @param path Yol
     * @return Uzunluk
     */
    public static double calculatePathLength(Path path) {
        if (path == null || path.isEmpty()) {
            return 0;
        }

        double length = 0;
        List<Position> positions = path.getPositions();

        for (int i = 0; i < positions.size() - 1; i++) {
            length += positions.get(i).euclideanDistance(positions.get(i + 1));
        }

        return length;
    }

    /**
     * İki path'i karşılaştırır
     * @param path1 İlk yol
     * @param path2 İkinci yol
     * @return 0 ise eşit, -1 ise path1 kısa, 1 ise path2 kısa
     */
    public static int comparePaths(Path path1, Path path2) {
        return Integer.compare(path1.getLength(), path2.getLength());
    }

    /**
     * Path'i optimize eder (gereksiz adımları çıkarır)
     * @param path Yol
     * @return Optimize edilmiş yol
     */
    public static Path optimizePath(Path path) {
        if (path == null || path.getLength() <= 2) {
            return path;
        }

        List<Position> positions = new ArrayList<>(path.getPositions());
        List<Position> optimized = new ArrayList<>();
        optimized.add(positions.get(0));

        int i = 0;
        while (i < positions.size() - 1) {
            int j = i + 1;

            // Düz çizgi üzerinde ilerlediğimiz sürece atla
            while (j < positions.size() - 1 && isInLine(positions.get(i), positions.get(j), positions.get(j + 1))) {
                j++;
            }

            optimized.add(positions.get(j));
            i = j;
        }

        return new Path(optimized, path.getCost(), path.getComputationTimeMs());
    }

    /**
     * Üç pozisyon aynı çizgi üzerinde mi kontrol eder
     * @param a İlk pozisyon
     * @param b İkinci pozisyon
     * @param c Üçüncü pozisyon
     * @return true ise aynı çizgide
     */
    private static boolean isInLine(Position a, Position b, Position c) {
        // Yatay çizgi
        if (a.getRow() == b.getRow() && b.getRow() == c.getRow()) {
            return true;
        }

        // Dikey çizgi
        if (a.getCol() == b.getCol() && b.getCol() == c.getCol()) {
            return true;
        }

        return false;
    }

    /**
     * Path'i ters çevirir
     * @param path Yol
     * @return Ters çevrilmiş yol
     */
    public static Path reversePath(Path path) {
        if (path == null) {
            return null;
        }

        List<Position> positions = new ArrayList<>(path.getPositions());
        Collections.reverse(positions);

        return new Path(positions, path.getCost(), path.getComputationTimeMs());
    }
}
