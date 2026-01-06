package com.maze.generator;

import com.maze.core.IMazeGenerator;
import com.maze.model.*;
import com.maze.util.UnionFind;

import java.util.*;

/**
 * Maze generator using Kruskal's Minimum Spanning Tree algorithm.
 *
 * Algorithm:
 * 1. Each cell starts in its own disjoint set
 * 2. All possible walls (edges) are collected
 * 3. Edges are shuffled randomly
 * 4. For each edge:
 *    - If the two cells are in different sets, remove the wall and union them
 * 5. Continue until all cells are connected
 *
 * Properties:
 * - Grid-based
 * - Cells are located at odd indices
 * - Walls are located at even indices
 *
 * Time Complexity: O(E α(V))
 * Space Complexity: O(V + E)
 */
public class KruskalMazeGenerator implements IMazeGenerator {

    /* ===================== EDGE ===================== */
    private static class Edge {
        Position cellA;
        Position cellB;
        Position wallBetween;

        Edge(Position a, Position b, Position wall) {
            this.cellA = a;
            this.cellB = b;
            this.wallBetween = wall;
        }
    }

    /* ===================== GENERATE ===================== */
    @Override
    public Maze generate(int rows, int cols) {

        // Ensure odd dimensions
        if (rows % 2 == 0) rows++;
        if (cols % 2 == 0) cols++;

        Maze maze = new Maze(rows, cols);
        initializeWalls(maze);

        // Union-Find over entire grid (only odd cells are used)
        UnionFind uf = new UnionFind(rows * cols);

        // Initialize cells
        for (int r = 1; r < rows; r += 2) {
            for (int c = 1; c < cols; c += 2) {
                maze.setCell(r, c, Cell.Type.PATH);
            }
        }

        // Build and shuffle edges
        List<Edge> edges = buildEdges(rows, cols);
        Collections.shuffle(edges, new Random());

        // Kruskal MST
        for (Edge edge : edges) {
            int idA = cellIndex(edge.cellA.getRow(), edge.cellA.getCol(), cols);
            int idB = cellIndex(edge.cellB.getRow(), edge.cellB.getCol(), cols);

            if (uf.union(idA, idB)) {
                maze.setCell(
                        edge.wallBetween.getRow(),
                        edge.wallBetween.getCol(),
                        Cell.Type.PATH
                );
            }
        }

        // Start & End
        setStartAndEnd(maze);

        return maze;
    }

    /* ===================== GENERATE WITH STEPS ===================== */
    @Override
    public List<AlgorithmStep> generateWithSteps(int rows, int cols) {

        List<AlgorithmStep> steps = new ArrayList<>();

        if (rows % 2 == 0) rows++;
        if (cols % 2 == 0) cols++;

        Maze maze = new Maze(rows, cols);
        initializeWalls(maze);

        UnionFind uf = new UnionFind(rows * cols);

        // Initialize cells
        for (int r = 1; r < rows; r += 2) {
            for (int c = 1; c < cols; c += 2) {
                maze.setCell(r, c, Cell.Type.PATH);
            }
        }

        List<Edge> edges = buildEdges(rows, cols);
        Collections.shuffle(edges, new Random());

        int stepCount = 0;

        for (Edge edge : edges) {
            int idA = cellIndex(edge.cellA.getRow(), edge.cellA.getCol(), cols);
            int idB = cellIndex(edge.cellB.getRow(), edge.cellB.getCol(), cols);

            if (uf.union(idA, idB)) {
                maze.setCell(
                        edge.wallBetween.getRow(),
                        edge.wallBetween.getCol(),
                        Cell.Type.PATH
                );

                steps.add(new AlgorithmStep(
                        AlgorithmStep.StepType.VISIT,
                        edge.wallBetween,
                        Arrays.asList(edge.cellA, edge.cellB),
                        "Step " + (++stepCount) +
                                ": Removed wall between " +
                                edge.cellA + " and " + edge.cellB
                ));
            }
        }

        setStartAndEnd(maze);

        steps.add(new AlgorithmStep(
                AlgorithmStep.StepType.COMPLETE,
                maze.getEndPosition(),
                new ArrayList<>(),
                "Maze generation completed!"
        ));

        return steps;
    }

    /* ===================== HELPERS ===================== */

    private List<Edge> buildEdges(int rows, int cols) {
        List<Edge> edges = new ArrayList<>();

        for (int r = 1; r < rows; r += 2) {
            for (int c = 1; c < cols; c += 2) {
                Position cell = new Position(r, c);

                // Right neighbor
                if (c + 2 < cols) {
                    edges.add(new Edge(
                            cell,
                            new Position(r, c + 2),
                            new Position(r, c + 1)
                    ));
                }

                // Bottom neighbor
                if (r + 2 < rows) {
                    edges.add(new Edge(
                            cell,
                            new Position(r + 2, c),
                            new Position(r + 1, c)
                    ));
                }
            }
        }
        return edges;
    }

    private void initializeWalls(Maze maze) {
        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                maze.setCell(r, c, Cell.Type.WALL);
            }
        }
    }

    private int cellIndex(int row, int col, int cols) {
        return row * cols + col;
    }

    private void setStartAndEnd(Maze maze) {
        Position start = new Position(1, 1);
        Position end = new Position(
                maze.getRows() - 2,
                maze.getCols() - 2
        );

        maze.setStartPosition(start);
        maze.setEndPosition(end);

        maze.setCell(start.getRow(), start.getCol(), Cell.Type.START);
        maze.setCell(end.getRow(), end.getCol(), Cell.Type.END);
    }

    /* ===================== META ===================== */

    @Override
    public String getAlgorithmName() {
        return "Kruskal's Algorithm";
    }

    @Override
    public String getTimeComplexity() {
        return "O(E α(V))";
    }

    @Override
    public String getSpaceComplexity() {
        return "O(V + E)";
    }
}
