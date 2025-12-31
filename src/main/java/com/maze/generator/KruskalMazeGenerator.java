package com.maze.generator;

import com.maze.core.IMazeGenerator;
import com.maze.model.*;
import com.maze.util.UnionFind;
import java.util.*;

/**
 * Kruskal's Minimum Spanning Tree algoritması ile labirent üretir.
 *
 * Algoritma:
 * 1. Tüm hücreler ayrı set'lerde başlar
 * 2. Tüm olası duvarları (edge) listeye ekle
 * 3. Duvarları rastgele karıştır
 * 4. Her duvar için: eğer iki farklı set'i birleştiriyorsa, duvarı kaldır
 * 5. Tüm hücreler tek bir set'te olana kadar devam et
 *
 * Time Complexity: O(E log E) - E = edge sayısı
 * Space Complexity: O(V + E) - V = vertex sayısı
 */
public class KruskalMazeGenerator implements IMazeGenerator {

    @Override
    public Maze generate(int rows, int cols) {

        int mazeRows = rows * 2 + 1;
        int mazeCols = cols * 2 + 1;

        Maze maze = new Maze(mazeRows, mazeCols);

        // 1. Fill everything with WALL
        for (int r = 0; r < mazeRows; r++) {
            for (int c = 0; c < mazeCols; c++) {
                maze.setCell(r, c, Cell.Type.WALL);
            }
        }

        // 2. Open all room cells (odd, odd)
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                maze.setCell(2 * r + 1, 2 * c + 1, Cell.Type.PATH);
            }
        }

        // 3. Union-Find for logical cells
        UnionFind uf = new UnionFind(rows * cols);

        // 4. Generate walls between logical cells
        List<Edge> edges = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                int index = r * cols + c;

                // Right neighbor
                if (c < cols - 1) {
                    edges.add(new Edge(
                            new Position(r, c),
                            new Position(r, c + 1),
                            Math.random()
                    ));
                }

                // Bottom neighbor
                if (r < rows - 1) {
                    edges.add(new Edge(
                            new Position(r, c),
                            new Position(r + 1, c),
                            Math.random()
                    ));
                }
            }
        }

        Collections.shuffle(edges);

        // 5. Kruskal: remove walls
        for (Edge edge : edges) {
            Position a = edge.getFrom();
            Position b = edge.getTo();

            int cellA = a.getRow() * cols + a.getCol();
            int cellB = b.getRow() * cols + b.getCol();

            if (uf.union(cellA, cellB)) {

                int wallRow = a.getRow() + b.getRow() + 1;
                int wallCol = a.getCol() + b.getCol() + 1;

                maze.setCell(wallRow, wallCol, Cell.Type.PATH);
            }
        }

        // 6. Start & End
        maze.setStartPosition(new Position(1, 1));
        maze.setEndPosition(new Position(mazeRows - 2, mazeCols - 2));

        maze.setCell(1, 1, Cell.Type.START);
        maze.setCell(mazeRows - 2, mazeCols - 2, Cell.Type.END);

        return maze;
    }


    @Override
    public List<AlgorithmStep> generateWithSteps(int rows, int cols) {
        List<AlgorithmStep> steps = new ArrayList<>();
        Maze maze = new Maze(rows, cols);

        initializeWalls(maze);
        UnionFind uf = new UnionFind(rows * cols);
        List<Edge> edges = generateEdges(rows, cols);
        Collections.shuffle(edges);

        int stepCount = 0;
        for (Edge edge : edges) {
            Position from = edge.getFrom();
            Position to = edge.getTo();

            int cell1 = positionToIndex(from, cols);
            int cell2 = positionToIndex(to, cols);

            if (uf.union(cell1, cell2)) {
                maze.setCell(from.getRow(), from.getCol(), Cell.Type.PATH);
                maze.setCell(to.getRow(), to.getCol(), Cell.Type.PATH);

                steps.add(new AlgorithmStep(
                        AlgorithmStep.StepType.VISIT,
                        from,
                        Arrays.asList(from, to),
                        "Step " + (++stepCount) + ": Connected " + from + " and " + to
                ));
            }
        }

        steps.add(new AlgorithmStep(
                AlgorithmStep.StepType.COMPLETE,
                new Position(rows - 1, cols - 1),
                new ArrayList<>(),
                "Maze generation completed!"
        ));

        return steps;
    }

    private void initializeWalls(Maze maze) {
        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                maze.getCell(r, c).setType(Cell.Type.WALL); // ✓ Doğru
            }
        }
    }

    private List<Edge> generateEdges(int rows, int cols) {
        List<Edge> edges = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Position current = new Position(r, c);

                // Sağ komşu
                if (c < cols - 1) {
                    edges.add(new Edge(current, new Position(r, c + 1), Math.random()));
                }

                // Alt komşu
                if (r < rows - 1) {
                    edges.add(new Edge(current, new Position(r + 1, c), Math.random()));
                }
            }
        }

        return edges;
    }

    private int positionToIndex(Position pos, int cols) {
        return pos.getRow() * cols + pos.getCol();
    }

    @Override
    public String getAlgorithmName() {
        return "Kruskal's Algorithm";
    }

    @Override
    public String getTimeComplexity() {
        return "O(E log E)";
    }

    @Override
    public String getSpaceComplexity() {
        return "O(V + E)";
    }
}