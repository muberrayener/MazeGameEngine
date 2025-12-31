package com.maze.pathfinder;

import com.maze.core.IPathFinder;
import com.maze.model.*;
import com.maze.util.GraphUtils;
import java.util.*;

/**
 * A* (A-Star) Search ile yol bulma.
 *
 * Algoritma:
 * 1. f(n) = g(n) + h(n) hesapla
 *    - g(n): start'tan n'ye gerçek maliyet
 *    - h(n): n'den goal'a tahmini maliyet (heuristic)
 * 2. En düşük f değerli hücreyi seç
 * 3. Komşuları değerlendir ve güncelle
 * 4. Goal'a ulaşana kadar devam et
 *
 * Özellikler:
 * - OPTIMAL: Admissible heuristic ile en kısa yolu bulur
 * - Informed search (heuristic kullanır)
 * - BFS'ten daha hızlı
 *
 * Time Complexity: O(E log V) - priority queue ile
 * Space Complexity: O(V)
 */
public class AStarPathFinder implements IPathFinder {

    /**
     * Heuristic fonksiyon: Manhattan distance
     */
    private int heuristic(Position a, Position b) {
        return Math.abs(a.getRow() - b.getRow()) +
                Math.abs(a.getCol() - b.getCol());
    }

    @Override
    public Path findPath(Maze maze, Position start, Position end) {

        long startTime = System.currentTimeMillis();
        maze.resetCells();

        PriorityQueue<Cell> openSet =
                new PriorityQueue<>(Comparator.comparingInt(Cell::getFCost));

        Set<Position> openSetPos = new HashSet<>();
        Set<Position> closedSet = new HashSet<>();

        Cell startCell = maze.getCell(start);
        startCell.setGCost(0);
        startCell.setHCost(heuristic(start, end));
        startCell.setParent(null);

        openSet.offer(startCell);
        openSetPos.add(start);

        while (!openSet.isEmpty()) {

            Cell current = openSet.poll();
            Position currentPos = current.getPosition();
            openSetPos.remove(currentPos);

            if (currentPos.equals(end)) {
                long endTime = System.currentTimeMillis();
                Path path = GraphUtils.reconstructPath(current);
                return new Path(
                        path.getPositions(),
                        current.getGCost(),
                        endTime - startTime
                );
            }

            closedSet.add(currentPos);

            for (Cell neighbor : maze.getNeighbors(currentPos)) {

                Position np = neighbor.getPosition();

                if (!neighbor.isWalkable() || closedSet.contains(np)) {
                    continue;
                }

                int tentativeG = current.getGCost() + 1;

                if (tentativeG < neighbor.getGCost()) {

                    neighbor.setParent(current);
                    neighbor.setGCost(tentativeG);
                    neighbor.setHCost(heuristic(np, end));

                    if (openSetPos.contains(np)) {
                        openSet.remove(neighbor);
                    }

                    openSet.offer(neighbor);
                    openSetPos.add(np);
                }
            }
        }

        long endTime = System.currentTimeMillis();
        return new Path(new ArrayList<>(), 0, endTime - startTime);
    }


    @Override
    public List<AlgorithmStep> findPathWithSteps(Maze maze, Position start, Position end) {
        List<AlgorithmStep> steps = new ArrayList<>();

        if (maze == null || start == null || end == null) {
            return steps;
        }

        maze.resetCells();

        PriorityQueue<Cell> openSet = new PriorityQueue<>(
                Comparator.comparingInt(Cell::getFCost)
        );
        Set<Position> closedSet = new HashSet<>();

        Cell startCell = maze.getCell(start);
        startCell.setGCost(0);
        startCell.setHCost(heuristic(start, end));
        startCell.setParent(null);

        openSet.offer(startCell);

        steps.add(new AlgorithmStep(
                AlgorithmStep.StepType.VISIT,
                start,
                Arrays.asList(start),
                "A* started at " + start + " (h=" + startCell.getHCost() + ")"
        ));

        int stepCount = 0;

        while (!openSet.isEmpty()) {
            Cell current = openSet.poll();
            Position currentPos = current.getPosition();

            steps.add(new AlgorithmStep(
                    AlgorithmStep.StepType.EXPLORE,
                    currentPos,
                    new ArrayList<>(),
                    String.format("Step %d: Exploring %s (g=%d, h=%d, f=%d)",
                            ++stepCount, currentPos,
                            current.getGCost(), current.getHCost(), current.getFCost())
            ));

            if (currentPos.equals(end)) {
                Path path = GraphUtils.reconstructPath(current);
                steps.add(new AlgorithmStep(
                        AlgorithmStep.StepType.COMPLETE,
                        end,
                        path.getPositions(),
                        "Path found! Length: " + path.getLength() + ", Cost: " + current.getGCost()
                ));
                return steps;
            }

            closedSet.add(currentPos);

            List<Cell> neighbors = maze.getNeighbors(currentPos);
            for (Cell neighbor : neighbors) {
                Position neighborPos = neighbor.getPosition();

                if (!neighbor.isWalkable() || closedSet.contains(neighborPos)) {
                    continue;
                }

                int tentativeGCost = current.getGCost() + 1;

                if (tentativeGCost < neighbor.getGCost()) {
                    neighbor.setParent(current);
                    neighbor.setGCost(tentativeGCost);
                    neighbor.setHCost(heuristic(neighborPos, end));

                    if (!openSet.contains(neighbor)) {
                        openSet.offer(neighbor);

                        steps.add(new AlgorithmStep(
                                AlgorithmStep.StepType.VISIT,
                                neighborPos,
                                new ArrayList<>(),
                                String.format("Added to open set: %s (f=%d)",
                                        neighborPos, neighbor.getFCost())
                        ));
                    }
                }
            }
        }

        steps.add(new AlgorithmStep(
                AlgorithmStep.StepType.COMPLETE,
                end,
                new ArrayList<>(),
                "No path found!"
        ));

        return steps;
    }

    @Override
    public Path findPathMultiTarget(Maze maze, Position start, List<Position> targets) {
        // A* için optimal multi-target routing (TSP-like)
        List<Position> completePath = new ArrayList<>();
        completePath.add(start);

        Position current = start;
        Set<Position> visited = new HashSet<>();

        // Her adımda en yakın hedefe git (greedy heuristic)
        while (visited.size() < targets.size()) {
            Position nearest = null;
            int minDistance = Integer.MAX_VALUE;

            for (Position target : targets) {
                if (!visited.contains(target)) {
                    int distance = heuristic(current, target);
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearest = target;
                    }
                }
            }

            if (nearest == null) break;

            Path path = findPath(maze, current, nearest);
            if (!path.isEmpty()) {
                completePath.addAll(path.getPositions().subList(1, path.getLength()));
                current = nearest;
                visited.add(nearest);
            } else {
                break;
            }
        }

        return new Path(completePath);
    }

    @Override
    public String getAlgorithmName() {
        return "A* Search (A-Star)";
    }

    @Override
    public boolean isOptimal() {
        return true;
    }

    @Override
    public String getTimeComplexity() {
        return "O(E log V)";
    }

    @Override
    public String getSpaceComplexity() {
        return "O(V)";
    }
}