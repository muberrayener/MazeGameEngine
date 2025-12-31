package com.maze.generator;

import com.maze.core.IMazeGenerator;
import com.maze.model.*;
import java.util.*;

/**
 * Prim's Minimum Spanning Tree algoritması ile labirent üretir.
 *
 * Algoritma:
 * 1. Rastgele bir başlangıç hücresi seç
 * 2. Bu hücreyi PATH yap ve duvarlarını listeye ekle
 * 3. Listeden rastgele bir duvar seç
 * 4. Eğer duvar bir PATH hücresini bir WALL hücresine bağlıyorsa:
 *    - Duvarı kaldır
 *    - WALL hücresini PATH yap
 *    - Yeni hücrenin duvarlarını listeye ekle
 * 5. Liste boşalana kadar devam et
 *
 * Time Complexity: O(V^2) - basit implementasyon
 * Space Complexity: O(V)
 */
public class PrimMazeGenerator implements IMazeGenerator {

    private static class Wall {
        Position cell;
        Position neighbor;

        Wall(Position cell, Position neighbor) {
            this.cell = cell;
            this.neighbor = neighbor;
        }
    }

    @Override
    public Maze generate(int rows, int cols) {
        Maze maze = new Maze(rows, cols);
        Random random = new Random();

        // Tüm hücreleri duvar yap
        initializeWalls(maze);

        // Rastgele başlangıç hücresi
        int startRow = random.nextInt(rows);
        int startCol = random.nextInt(cols);
        Position start = new Position(startRow, startCol);

        maze.setCell(startRow, startCol, Cell.Type.PATH);

        // Duvar listesi
        List<Wall> walls = new ArrayList<>();
        addWalls(maze, start, walls);

        // Prim algoritması
        while (!walls.isEmpty()) {
            // Rastgele bir duvar seç
            int index = random.nextInt(walls.size());
            Wall wall = walls.remove(index);

            Position neighbor = wall.neighbor;
            Cell neighborCell = maze.getCell(neighbor);

            // Eğer komşu hala duvar ise
            if (neighborCell != null && neighborCell.getType() == Cell.Type.WALL) {
                // Hücreyi PATH yap
                maze.setCell(neighbor.getRow(), neighbor.getCol(), Cell.Type.PATH);

                // Yeni hücrenin duvarlarını ekle
                addWalls(maze, neighbor, walls);
            }
        }

        // Start ve end pozisyonları
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
        Random random = new Random();

        initializeWalls(maze);

        int startRow = random.nextInt(rows);
        int startCol = random.nextInt(cols);
        Position start = new Position(startRow, startCol);

        maze.setCell(startRow, startCol, Cell.Type.PATH);
        steps.add(new AlgorithmStep(
                AlgorithmStep.StepType.VISIT,
                start,
                Arrays.asList(start),
                "Starting at " + start
        ));

        List<Wall> walls = new ArrayList<>();
        addWalls(maze, start, walls);

        int stepCount = 0;
        while (!walls.isEmpty()) {
            int index = random.nextInt(walls.size());
            Wall wall = walls.remove(index);
            Position neighbor = wall.neighbor;
            Cell neighborCell = maze.getCell(neighbor);

            if (neighborCell != null && neighborCell.getType() == Cell.Type.WALL) {
                maze.setCell(neighbor.getRow(), neighbor.getCol(), Cell.Type.PATH);

                steps.add(new AlgorithmStep(
                        AlgorithmStep.StepType.VISIT,
                        neighbor,
                        Arrays.asList(wall.cell, neighbor),
                        "Step " + (++stepCount) + ": Added " + neighbor
                ));

                addWalls(maze, neighbor, walls);
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
                maze.setCell(r, c, Cell.Type.WALL);
            }
        }
    }

    private void addWalls(Maze maze, Position cell, List<Wall> walls) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            int newRow = cell.getRow() + dir[0];
            int newCol = cell.getCol() + dir[1];

            if (maze.isValid(newRow, newCol)) {
                Position neighbor = new Position(newRow, newCol);
                Cell neighborCell = maze.getCell(neighbor);

                if (neighborCell.getType() == Cell.Type.WALL) {
                    walls.add(new Wall(cell, neighbor));
                }
            }
        }
    }

    @Override
    public String getAlgorithmName() {
        return "Prim's Algorithm";
    }

    @Override
    public String getTimeComplexity() {
        return "O(V^2)";
    }

    @Override
    public String getSpaceComplexity() {
        return "O(V)";
    }
}