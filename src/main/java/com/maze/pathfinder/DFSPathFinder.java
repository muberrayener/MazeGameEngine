package com.maze.pathfinder;

import com.maze.core.IPathFinder;
import com.maze.model.*;
import com.maze.util.PathUtils;
import java.util.*;

/**
 * Depth-First Search ile yol bulma.
 *
 * Algoritma:
 * 1. Start'ı stack'e ekle
 * 2. Stack'ten bir hücre al
 * 3. Eğer hedef ise, yolu reconstruct et
 * 4. Komşuları stack'e ekle
 * 5. Stack boşalana kadar devam et
 *
 * Özellikler:
 * - NON-OPTIMAL: En kısa yolu garanti etmez
 * - Daha az memory kullanır
 * - Deep exploration
 *
 * Time Complexity: O(V + E)
 * Space Complexity: O(V)
 */
public class DFSPathFinder implements IPathFinder {

    @Override
    public Path findPath(Maze maze, Position start, Position end) {
        long startTime = System.currentTimeMillis();

        if (maze == null || start == null || end == null) {
            return new Path(new ArrayList<>());
        }

        maze.resetCells();

        Stack<Cell> stack = new Stack<>();
        Set<Position> visited = new HashSet<>();

        Cell startCell = maze.getCell(start);
        stack.push(startCell);
        visited.add(start);
        startCell.setParent(null);

        Cell endCell = null;

        while (!stack.isEmpty()) {
            Cell current = stack.pop();
            Position currentPos = current.getPosition();

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
                    stack.push(neighbor);
                }
            }
        }

        long endTime = System.currentTimeMillis();

        if (endCell == null) {
            return new Path(new ArrayList<>(), 0, endTime - startTime);
        }

        return PathUtils.reconstructPath(endCell);
    }

    @Override
    public List<AlgorithmStep> findPathWithSteps(Maze maze, Position start, Position end) {
        List<AlgorithmStep> steps = new ArrayList<>();

        if (maze == null || start == null || end == null) {
            return steps;
        }

        maze.resetCells();

        Stack<Cell> stack = new Stack<>();
        Set<Position> visited = new HashSet<>();

        Cell startCell = maze.getCell(start);
        stack.push(startCell);
        visited.add(start);
        startCell.setParent(null);

        steps.add(new AlgorithmStep(
                AlgorithmStep.StepType.VISIT,
                start,
                Arrays.asList(start),
                "DFS started at " + start
        ));

        Cell endCell = null;
        int stepCount = 0;

        while (!stack.isEmpty()) {
            Cell current = stack.pop();
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
                    stack.push(neighbor);

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
            Path path = PathUtils.reconstructPath(endCell);
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
        // DFS için basit sequential approach
        List<Position> completePath = new ArrayList<>();
        completePath.add(start);

        Position current = start;
        for (Position target : targets) {
            Path path = findPath(maze, current, target);
            if (!path.isEmpty()) {
                completePath.addAll(path.getPositions().subList(1, path.getLength()));
                current = target;
            }
        }

        return new Path(completePath);
    }

    @Override
    public String getAlgorithmName() {
        return "Depth-First Search (DFS)";
    }

    @Override
    public boolean isOptimal() {
        return false;
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