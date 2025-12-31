package com.maze.util;

import com.maze.model.*;
import java.util.*;

/**
 * Graf işlemleri için yardımcı metotlar.
 */
public class GraphUtils {

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