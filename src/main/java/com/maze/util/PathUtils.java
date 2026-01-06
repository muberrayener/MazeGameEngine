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

    /**
     * Maze'i graf'a dönüştürür
     * @param maze Labirent
     * @return Node listesi
     */
    public static List<Node> mazeToGraph(Maze maze) {
        List<Node> nodes = new ArrayList<>();
        Map<String, Node> nodeMap = new HashMap<>();

        // Tüm walkable hücreler için node oluştur
        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                Cell cell = maze.getCell(r, c);
                if (cell.isWalkable()) {
                    Position pos = cell.getPosition();
                    Node node = new Node(pos);
                    nodes.add(node);
                    nodeMap.put(positionKey(pos), node);
                }
            }
        }

        // Komşuları bağla
        for (Node node : nodes) {
            List<Cell> neighbors = maze.getNeighbors(node.getPosition());
            for (Cell neighbor : neighbors) {
                if (neighbor.isWalkable()) {
                    String key = positionKey(neighbor.getPosition());
                    Node neighborNode = nodeMap.get(key);
                    if (neighborNode != null) {
                        node.addNeighbor(neighborNode);
                    }
                }
            }
        }

        return nodes;
    }

    /**
     * Tüm olası kenarları üretir (MST için)
     * @param maze Labirent
     * @return Kenar listesi
     */
    public static List<Edge> generateAllEdges(Maze maze) {
        List<Edge> edges = new ArrayList<>();

        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                Position current = new Position(r, c);

                // Sağ komşu
                if (c < maze.getCols() - 1) {
                    Position right = new Position(r, c + 1);
                    edges.add(new Edge(current, right, Math.random()));
                }

                // Alt komşu
                if (r < maze.getRows() - 1) {
                    Position down = new Position(r + 1, c);
                    edges.add(new Edge(current, down, Math.random()));
                }
            }
        }

        return edges;
    }

    /**
     * Path'i Cell listesine dönüştürür
     * @param path Yol
     * @param maze Labirent
     * @return Cell listesi
     */
    public static List<Cell> pathToCells(Path path, Maze maze) {
        List<Cell> cells = new ArrayList<>();
        for (Position pos : path.getPositions()) {
            Cell cell = maze.getCell(pos);
            if (cell != null) {
                cells.add(cell);
            }
        }
        return cells;
    }

    /**
     * Cell'den Path oluşturur (parent takibi ile)
     * @param endCell Son hücre
     * @return Path
     */
    public static Path reconstructPath(Cell endCell) {
        List<Position> positions = new ArrayList<>();
        Cell current = endCell;

        while (current != null) {
            positions.add(0, current.getPosition());
            current = current.getParent();
        }

        return new Path(positions);
    }

    /**
     * İki pozisyon arasındaki Manhattan distance
     * @param a İlk pozisyon
     * @param b İkinci pozisyon
     * @return Manhattan distance
     */
    public static int manhattanDistance(Position a, Position b) {
        return Math.abs(a.getRow() - b.getRow()) +
                Math.abs(a.getCol() - b.getCol());
    }

    /**
     * İki pozisyon arasındaki Euclidean distance
     * @param a İlk pozisyon
     * @param b İkinci pozisyon
     * @return Euclidean distance
     */
    public static double euclideanDistance(Position a, Position b) {
        int dx = a.getRow() - b.getRow();
        int dy = a.getCol() - b.getCol();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Maze'de kaç tane walkable hücre var
     * @param maze Labirent
     * @return Walkable hücre sayısı
     */
    public static int countWalkableCells(Maze maze) {
        int count = 0;
        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                if (maze.getCell(r, c).isWalkable()) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Position için unique key üretir
     * @param pos Pozisyon
     * @return String key
     */
    private static String positionKey(Position pos) {
        return pos.getRow() + "," + pos.getCol();
    }

    /**
     * Maze'in connected olup olmadığını kontrol eder (BFS ile)
     * @param maze Labirent
     * @param start Başlangıç pozisyonu
     * @return true ise tüm walkable hücreler erişilebilir
     */
    public static boolean isConnected(Maze maze, Position start) {
        if (!maze.isValid(start) || !maze.getCell(start).isWalkable()) {
            return false;
        }

        int walkableCount = countWalkableCells(maze);
        Set<Position> visited = new HashSet<>();
        Queue<Position> queue = new LinkedList<>();

        queue.offer(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Position current = queue.poll();
            List<Cell> neighbors = maze.getNeighbors(current);

            for (Cell neighbor : neighbors) {
                Position neighborPos = neighbor.getPosition();
                if (neighbor.isWalkable() && !visited.contains(neighborPos)) {
                    visited.add(neighborPos);
                    queue.offer(neighborPos);
                }
            }
        }

        return visited.size() == walkableCount;
    }

    /**
     * Random walkable pozisyon üretir
     * @param maze Labirent
     * @return Random position (veya null)
     */
    public static Position getRandomWalkablePosition(Maze maze) {
        Random random = new Random();
        int maxAttempts = 100;

        for (int i = 0; i < maxAttempts; i++) {
            int row = random.nextInt(maze.getRows());
            int col = random.nextInt(maze.getCols());
            Cell cell = maze.getCell(row, col);

            if (cell != null && cell.isWalkable()) {
                return cell.getPosition();
            }
        }

        return null;
    }
}
