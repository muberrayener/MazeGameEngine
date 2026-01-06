package com.maze.generator;

import com.maze.core.IMazeGenerator;
import com.maze.model.*;

import java.util.*;

/**
 * Prim's Minimum Spanning Tree algoritması ile labirent üretir.
 *
 * Algoritma:
 * 1. Rastgele bir başlangıç hücresi seç ve PATH olarak işaretle
 * 2. Bu hücrenin etrafındaki duvarları (komşu hücrelerle bağlantı) bir listeye ekle
 * 3. Liste boşalana kadar:
 *    a. Listeden rastgele bir duvar seç
 *    b. Eğer bu duvar PATH olan bir hücreyi WALL olan bir hücreye bağlıyorsa:
 *       - Duvarı kaldır
 *       - WALL hücresini PATH yap
 *       - Yeni PATH hücresinin komşu duvarlarını listeye ekle
 *
 * Özellikler:
 * - Grid-based: kullanıcıdan alınan boyut final maze grid boyutu
 * - Hücreler tek indekslerde, duvarlar çift indekslerde
 * - Rastgele seçim ile farklı labirentler üretilebilir
 *
 * Time Complexity: O(V) - her hücre en fazla 4 defa işlenir
 * Space Complexity: O(V) - duvar listesi ve maze matrisi
 */
public class PrimMazeGenerator implements IMazeGenerator {

    private static class Wall {
        Position from;
        Position to;
        Position between;

        Wall(Position from, Position to, Position between) {
            this.from = from;
            this.to = to;
            this.between = between;
        }
    }

    @Override
    public Maze generate(int rows, int cols) {

        // Enforce odd dimensions
        if (rows % 2 == 0) rows++;
        if (cols % 2 == 0) cols++;

        Maze maze = new Maze(rows, cols);
        Random random = new Random();

        initializeWalls(maze);

        // Start at odd cell
        int startRow = randomOdd(rows, random);
        int startCol = randomOdd(cols, random);
        Position start = new Position(startRow, startCol);

        maze.setCell(startRow, startCol, Cell.Type.PATH);

        List<Wall> walls = new ArrayList<>();
        addWalls(maze, start, walls);

        while (!walls.isEmpty()) {
            Wall wall = walls.remove(random.nextInt(walls.size()));

            Cell target = maze.getCell(wall.to);
            if (target.getType() == Cell.Type.WALL) {

                // Carve passage
                maze.setCell(wall.between.getRow(), wall.between.getCol(), Cell.Type.PATH);
                maze.setCell(wall.to.getRow(), wall.to.getCol(), Cell.Type.PATH);

                addWalls(maze, wall.to, walls);
            }
        }

        maze.setStartPosition(start);
        maze.setEndPosition(new Position(rows - 2, cols - 2));
        maze.setCell(start.getRow(), start.getCol(), Cell.Type.START);
        maze.setCell(rows - 2, cols - 2, Cell.Type.END);

        return maze;
    }

    @Override
    public List<AlgorithmStep> generateWithSteps(int rows, int cols) {

        List<AlgorithmStep> steps = new ArrayList<>();

        if (rows % 2 == 0) rows++;
        if (cols % 2 == 0) cols++;

        Maze maze = new Maze(rows, cols);
        Random random = new Random();

        initializeWalls(maze);

        int startRow = randomOdd(rows, random);
        int startCol = randomOdd(cols, random);
        Position start = new Position(startRow, startCol);

        maze.setCell(startRow, startCol, Cell.Type.PATH);

        steps.add(new AlgorithmStep(
                AlgorithmStep.StepType.VISIT,
                start,
                List.of(start),
                "Start cell selected"
        ));

        List<Wall> walls = new ArrayList<>();
        addWalls(maze, start, walls);

        int stepCount = 0;

        while (!walls.isEmpty()) {
            Wall wall = walls.remove(random.nextInt(walls.size()));

            if (maze.getCell(wall.to).getType() == Cell.Type.WALL) {

                maze.setCell(wall.between.getRow(), wall.between.getCol(), Cell.Type.PATH);
                maze.setCell(wall.to.getRow(), wall.to.getCol(), Cell.Type.PATH);

                steps.add(new AlgorithmStep(
                        AlgorithmStep.StepType.VISIT,
                        wall.to,
                        Arrays.asList(wall.between, wall.to),
                        "Step " + (++stepCount) + ": Carved passage"
                ));

                addWalls(maze, wall.to, walls);
            }
        }

        Position end = new Position(rows - 2, cols - 2);
        maze.setStartPosition(start);
        maze.setEndPosition(end);

        maze.setCell(start.getRow(), start.getCol(), Cell.Type.START);
        maze.setCell(end.getRow(), end.getCol(), Cell.Type.END);

        steps.add(new AlgorithmStep(
                AlgorithmStep.StepType.COMPLETE,
                end,
                new ArrayList<>(),
                "Maze generation completed"
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
        int[][] dirs = {{-2,0},{2,0},{0,-2},{0,2}};

        for (int[] d : dirs) {
            int nr = cell.getRow() + d[0];
            int nc = cell.getCol() + d[1];

            if (maze.isValid(nr, nc)) {
                Position to = new Position(nr, nc);
                Position between = new Position(
                        cell.getRow() + d[0] / 2,
                        cell.getCol() + d[1] / 2
                );

                if (maze.getCell(to).getType() == Cell.Type.WALL) {
                    walls.add(new Wall(cell, to, between));
                }
            }
        }
    }

    private int randomOdd(int limit, Random rnd) {
        int r;
        do {
            r = rnd.nextInt(limit);
        } while (r % 2 == 0);
        return r;
    }

    @Override
    public String getAlgorithmName() {
        return "Prim's Algorithm";
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
