package com.maze.generator;

import com.maze.core.IMazeGenerator;
import com.maze.model.*;

import java.util.*;

/**
 * Recursive Backtracking (DFS-based) ile labirent üretir.
 *
 * Algoritma:
 * 1. Rastgele bir başlangıç hücresi seç ve PATH olarak işaretle
 * 2. Ziyaret edilmemiş rastgele bir komşu seç
 * 3. Aralarındaki duvarı kaldır
 * 4. Komşu için recursive olarak devam et
 * 5. Ziyaret edilmemiş komşu yoksa geri dön (backtrack)
 *
 * Özellikler:
 * - Grid-based: hücreler tek indekslerde, duvarlar çift indekslerde
 * - Rastgele seçim ile farklı labirentler üretilebilir
 * - Hem generate() hem de generateWithSteps() destekler
 *
 * Time Complexity: O(V) - her hücre bir kez ziyaret edilir
 * Space Complexity: O(V) - recursion stack
 */

public class RecursiveBacktracker implements IMazeGenerator {

    private Random random = new Random();
    private boolean[][] visited;

    @Override
    public Maze generate(int rows, int cols) {
        if (rows % 2 == 0) rows++;
        if (cols % 2 == 0) cols++;

        Maze maze = new Maze(rows, cols);
        initializeWalls(maze);
        visited = new boolean[rows][cols];

        // Başlangıç hücresi (odd indices)
        int startRow = randomOdd(rows);
        int startCol = randomOdd(cols);
        Position start = new Position(startRow, startCol);

        carve(maze, start);

        // Start & End
        Position end = new Position(rows - 2, cols - 2);
        maze.setStartPosition(start);
        maze.setEndPosition(end);
        maze.setCell(start.getRow(), start.getCol(), Cell.Type.START);
        maze.setCell(end.getRow(), end.getCol(), Cell.Type.END);

        return maze;
    }

    @Override
    public List<AlgorithmStep> generateWithSteps(int rows, int cols) {
        if (rows % 2 == 0) rows++;
        if (cols % 2 == 0) cols++;

        Maze maze = new Maze(rows, cols);
        initializeWalls(maze);
        visited = new boolean[rows][cols];

        List<AlgorithmStep> steps = new ArrayList<>();
        Stack<Position> path = new Stack<>();

        int startRow = randomOdd(rows);
        int startCol = randomOdd(cols);
        Position start = new Position(startRow, startCol);

        carveWithSteps(maze, start, steps, path);

        // Start & End
        Position end = new Position(rows - 2, cols - 2);
        maze.setStartPosition(start);
        maze.setEndPosition(end);
        maze.setCell(start.getRow(), start.getCol(), Cell.Type.START);
        maze.setCell(end.getRow(), end.getCol(), Cell.Type.END);

        steps.add(new AlgorithmStep(
                AlgorithmStep.StepType.COMPLETE,
                end,
                new ArrayList<>(),
                "Maze generation completed!"
        ));

        return steps;
    }

    /* ===================== CORE DFS ===================== */
    private void carve(Maze maze, Position current) {
        visited[current.getRow()][current.getCol()] = true;
        maze.setCell(current.getRow(), current.getCol(), Cell.Type.PATH);

        List<Position> neighbors = getUnvisitedNeighbors(maze, current);
        Collections.shuffle(neighbors, random);

        for (Position neighbor : neighbors) {
            if (!visited[neighbor.getRow()][neighbor.getCol()]) {
                // Remove wall between current and neighbor
                Position wall = new Position(
                        (current.getRow() + neighbor.getRow()) / 2,
                        (current.getCol() + neighbor.getCol()) / 2
                );
                maze.setCell(wall.getRow(), wall.getCol(), Cell.Type.PATH);

                carve(maze, neighbor);
            }
        }
    }

    private void carveWithSteps(Maze maze, Position current,
                                List<AlgorithmStep> steps, Stack<Position> path) {
        visited[current.getRow()][current.getCol()] = true;
        maze.setCell(current.getRow(), current.getCol(), Cell.Type.PATH);
        path.push(current);

        steps.add(new AlgorithmStep(
                AlgorithmStep.StepType.VISIT,
                current,
                new ArrayList<>(path),
                "Visiting " + current
        ));

        List<Position> neighbors = getUnvisitedNeighbors(maze, current);
        Collections.shuffle(neighbors, random);

        boolean hasUnvisited = false;
        for (Position neighbor : neighbors) {
            if (!visited[neighbor.getRow()][neighbor.getCol()]) {
                hasUnvisited = true;

                // Remove wall between current and neighbor
                Position wall = new Position(
                        (current.getRow() + neighbor.getRow()) / 2,
                        (current.getCol() + neighbor.getCol()) / 2
                );
                maze.setCell(wall.getRow(), wall.getCol(), Cell.Type.PATH);

                carveWithSteps(maze, neighbor, steps, path);
            }
        }

        if (!hasUnvisited && !path.isEmpty()) {
            path.pop();
            if (!path.isEmpty()) {
                steps.add(new AlgorithmStep(
                        AlgorithmStep.StepType.BACKTRACK,
                        current,
                        new ArrayList<>(path),
                        "Backtracking from " + current
                ));
            }
        }
    }

    /* ===================== HELPERS ===================== */
    private List<Position> getUnvisitedNeighbors(Maze maze, Position current) {
        List<Position> neighbors = new ArrayList<>();
        int[][] dirs = {{-2, 0}, {2, 0}, {0, -2}, {0, 2}}; // jump 2 to skip walls

        for (int[] d : dirs) {
            int nr = current.getRow() + d[0];
            int nc = current.getCol() + d[1];

            if (maze.isValid(nr, nc) && !visited[nr][nc]) {
                neighbors.add(new Position(nr, nc));
            }
        }

        return neighbors;
    }

    private void initializeWalls(Maze maze) {
        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                maze.setCell(r, c, Cell.Type.WALL);
            }
        }
    }

    private int randomOdd(int limit) {
        int r;
        do {
            r = random.nextInt(limit);
        } while (r % 2 == 0);
        return r;
    }

    @Override
    public String getAlgorithmName() {
        return "Recursive Backtracker";
    }

    @Override
    public String getTimeComplexity() {
        return "O(V)";
    }

    @Override
    public String getSpaceComplexity() {
        return "O(V)";
    }
}
