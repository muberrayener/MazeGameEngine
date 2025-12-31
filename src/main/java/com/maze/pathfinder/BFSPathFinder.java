package com.maze.pathfinder;

import com.maze.core.IPathFinder;
import com.maze.model.*;
import com.maze.util.GraphUtils;
import java.util.*;

/**
 * Breadth-First Search ile yol bulma.
 *
 * Algoritma:
 * 1. Start'ı queue'ya ekle
 * 2. Queue'dan bir hücre al
 * 3. Eğer hedef ise, yolu reconstruct et
 * 4. Komşuları queue'ya ekle
 * 5. Queue boşalana kadar devam et
 *
 * Özellikler:
 * - OPTIMAL: Her zaman en kısa yolu bulur
 * - Unweighted graph için ideal
 * - Level-by-level arama
 *
 * Time Complexity: O(V + E)
 * Space Complexity: O(V)
 */
public class BFSPathFinder implements IPathFinder {

    @Override
    public Path findPath(Maze maze, Position start, Position end) {
        long startTime = System.currentTimeMillis();

        if (maze == null || start == null || end == null) {
            return new Path(new ArrayList<>());
        }

        maze.resetCells();

        Queue<Cell> queue = new LinkedList<>();
        Set<Position> visited = new HashSet<>();

        Cell startCell = maze.getCell(start);
        queue.offer(startCell);
        visited.add(start);
        startCell.setParent(null);

        Cell endCell = null;

        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            Position currentPos = current.getPosition();

            // Hedefe ulaştık mı?
            if (currentPos.equals(end)) {
                endCell = current;
                break;
            }

            // Komşuları işle
            List<Cell> neighbors = maze.getNeighbors(currentPos);
            for (Cell neighbor : neighbors) {
                Position neighborPos = neighbor.getPosition();

                if (neighbor.isWalkable() && !visited.contains(neighborPos)) {
                    visited.add(neighborPos);
                    neighbor.setParent(current);
                    queue.offer(neighbor);
                }
            }
        }

        long endTime = System.currentTimeMillis();

        // Yol bulunamadı
        if (endCell == null) {
            return new Path(new ArrayList<>(), 0, endTime - startTime);
        }

        // Path'i reconstruct et
        return GraphUtils.reconstructPath(endCell);
    }

    @Override
    public List<AlgorithmStep> findPathWithSteps(Maze maze, Position start, Position end) {
        List<AlgorithmStep> steps = new ArrayList<>();

        if (maze == null || start == null || end == null) {
            return steps;
        }

        maze.resetCells();

        Queue<Cell> queue = new LinkedList<>();
        Set<Position> visited = new HashSet<>();

        Cell startCell = maze.getCell(start);
        queue.offer(startCell);
        visited.add(start);
        startCell.setParent(null);

        steps.add(new AlgorithmStep(
                AlgorithmStep.StepType.VISIT,
                start,
                Arrays.asList(start),
                "BFS started at " + start
        ));

        Cell endCell = null;
        int stepCount = 0;

        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            Position currentPos = current.getPosition();

            steps.add(new AlgorithmStep(
                    AlgorithmStep.StepType.EXPLORE,
                    currentPos,
                    new ArrayList<>(),
                    "Step " + (++stepCount) + ": Exploring " + currentPos
            ));

            if (currentPos.equals(end)) {
                endCell = current;
                break;
            }

            List<Cell> neighbors = maze.getNeighbors(currentPos);
            for (Cell neighbor : neighbors) {
                Position neighborPos = neighbor.getPosition();

                if (neighbor.isWalkable() && !visited.contains(neighborPos)) {
                    visited.add(neighborPos);
                    neighbor.setParent(current);
                    queue.offer(neighbor);

                    steps.add(new AlgorithmStep(
                            AlgorithmStep.StepType.VISIT,
                            neighborPos,
                            new ArrayList<>(),
                            "Discovered " + neighborPos
                    ));
                }
            }
        }

        if (endCell != null) {
            Path path = GraphUtils.reconstructPath(endCell);
            steps.add(new AlgorithmStep(
                    AlgorithmStep.StepType.COMPLETE,
                    end,
                    path.getPositions(),
                    "Path found! Length: " + path.getLength()
            ));
        } else {
            steps.add(new AlgorithmStep(
                    AlgorithmStep.StepType.COMPLETE,
                    end,
                    new ArrayList<>(),
                    "No path found!"
            ));
        }

        return steps;
    }

    @Override
    public Path findPathMultiTarget(Maze maze, Position start, List<Position> targets) {
        if (targets.isEmpty()) {
            return new Path(new ArrayList<>());
        }

        List<Position> completePath = new ArrayList<>();
        completePath.add(start);

        Position current = start;
        Set<Position> visited = new HashSet<>();

        // Her hedef için en yakın olanı bul (greedy)
        while (visited.size() < targets.size()) {
            Position nearest = null;
            Path shortestPath = null;

            for (Position target : targets) {
                if (!visited.contains(target)) {
                    Path path = findPath(maze, current, target);
                    if (!path.isEmpty() &&
                            (shortestPath == null || path.getLength() < shortestPath.getLength())) {
                        nearest = target;
                        shortestPath = path;
                    }
                }
            }

            if (nearest == null) break;

            visited.add(nearest);
            completePath.addAll(shortestPath.getPositions().subList(1, shortestPath.getLength()));
            current = nearest;
        }

        return new Path(completePath);
    }

    @Override
    public String getAlgorithmName() {
        return "Breadth-First Search (BFS)";
    }

    @Override
    public boolean isOptimal() {
        return true;
    }

    @Override
    public String getTimeComplexity() {
        return "O(V + E)";
    }

    @Override
    public String getSpaceComplexity() {
        return "O(V)";
    }
}