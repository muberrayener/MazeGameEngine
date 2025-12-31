package com.maze.generator;

import com.maze.core.IMazeGenerator;
import com.maze.model.*;
import java.util.*;

/**
 * Recursive Backtracking (DFS-based) ile labirent üretir.
 *
 * Algoritma:
 * 1. Rastgele bir başlangıç hücresi seç
 * 2. Hücreyi ziyaret edildi olarak işaretle
 * 3. Ziyaret edilmemiş rastgele bir komşu seç
 * 4. Aralarındaki duvarı kaldır
 * 5. Komşu için recursive olarak devam et
 * 6. Ziyaret edilmemiş komşu yoksa, geri dön (backtrack)
 *
 * Time Complexity: O(V) - her hücre bir kez ziyaret edilir
 * Space Complexity: O(V) - recursion stack
 */
public class RecursiveBacktracker implements IMazeGenerator {

    private Random random = new Random();
    private boolean[][] visited;

    @Override
    public Maze generate(int rows, int cols) {
        Maze maze = new Maze(rows, cols);

        // Tüm hücreleri duvar yap
        initializeWalls(maze);

        // Visited matrix
        visited = new boolean[rows][cols];

        // Rastgele başlangıç
        int startRow = random.nextInt(rows);
        int startCol = random.nextInt(cols);
        Position start = new Position(startRow, startCol);

        // Recursive DFS
        carve(maze, start);

        // Start ve end
        maze.setStartPosition(new Position(0, 0));
        maze.setEndPosition(new Position(rows - 1, cols - 1));
        maze.setCell(0, 0, Cell.Type.START);
        maze.setCell(rows - 1, cols - 1, Cell.Type.END);

        return maze;
    }

    @Override
    public List<AlgorithmStep> generateWithSteps(int rows, int cols) {
        List<AlgorithmStep> steps = new ArrayList<>();
        Maze maze = new Maze(rows, cols);

        initializeWalls(maze);
        visited = new boolean[rows][cols];

        int startRow = random.nextInt(rows);
        int startCol = random.nextInt(cols);
        Position start = new Position(startRow, startCol);

        carveWithSteps(maze, start, steps, new Stack<>());

        steps.add(new AlgorithmStep(
                AlgorithmStep.StepType.COMPLETE,
                new Position(rows - 1, cols - 1),
                new ArrayList<>(),
                "Maze generation completed!"
        ));

        return steps;
    }

    private void carve(Maze maze, Position current) {
        int row = current.getRow();
        int col = current.getCol();

        // Hücreyi ziyaret et
        visited[row][col] = true;
        maze.setCell(row, col, Cell.Type.PATH);

        // Komşuları rastgele sırala
        List<Position> neighbors = getUnvisitedNeighbors(maze, current);
        Collections.shuffle(neighbors, random);

        // Her komşu için recursive olarak devam et
        for (Position neighbor : neighbors) {
            if (!visited[neighbor.getRow()][neighbor.getCol()]) {
                carve(maze, neighbor);
            }
        }
    }

    private void carveWithSteps(Maze maze, Position current,
                                List<AlgorithmStep> steps, Stack<Position> path) {
        int row = current.getRow();
        int col = current.getCol();

        visited[row][col] = true;
        maze.setCell(row, col, Cell.Type.PATH);
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
                carveWithSteps(maze, neighbor, steps, path);
            }
        }

        if (!hasUnvisited && !path.isEmpty()) {
            path.pop();
            steps.add(new AlgorithmStep(
                    AlgorithmStep.StepType.BACKTRACK,
                    current,
                    new ArrayList<>(path),
                    "Backtracking from " + current
            ));
        }
    }

    private List<Position> getUnvisitedNeighbors(Maze maze, Position current) {
        List<Position> neighbors = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            int newRow = current.getRow() + dir[0];
            int newCol = current.getCol() + dir[1];

            if (maze.isValid(newRow, newCol) && !visited[newRow][newCol]) {
                neighbors.add(new Position(newRow, newCol));
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

    @Override
    public String getAlgorithmName() {
        return "Recursive Backtracker (DFS)";
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