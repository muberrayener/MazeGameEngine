package com.maze.pathfinder;

import com.maze.core.IPathFinder;
import com.maze.model.*;
import com.maze.util.PathUtils;

import java.util.*;

/**
 * A* (A-Star) Search - TreeSet (Red-Black Tree) kullanarak
 *
 * Open Set:
 *  - TreeSet<Cell>  -> O(log n) ekleme / silme
 *  - Map<Position, Cell> -> hızlı erişim
 *
 * Time Complexity: O(E log V)
 * Space Complexity: O(V)
 */
public class AStarPathFinder implements IPathFinder {

    /**
     * Manhattan Distance heuristic
     */
    private int heuristic(Position a, Position b) {
        return Math.abs(a.getRow() - b.getRow()) +
                Math.abs(a.getCol() - b.getCol());
    }

    /**
     * TreeSet için comparator (tie-breaker)
     */
    private final Comparator<Cell> cellComparator =
            Comparator.comparingInt(Cell::getFCost)
                    .thenComparingInt(Cell::getHCost)
                    .thenComparingInt(a -> a.getPosition().getRow())
                    .thenComparingInt(a -> a.getPosition().getCol());

    @Override
    public Path findPath(Maze maze, Position start, Position end) {

        long startTime = System.currentTimeMillis();
        maze.resetCells();

        // Open Set (Red-Black Tree)
        TreeSet<Cell> openSet = new TreeSet<>(cellComparator);
        Map<Position, Cell> openMap = new HashMap<>();

        // Closed Set
        Set<Position> closedSet = new HashSet<>();

        Cell startCell = maze.getCell(start);
        startCell.setGCost(0);
        startCell.setHCost(heuristic(start, end));
        startCell.setParent(null);

        openSet.add(startCell);
        openMap.put(start, startCell);

        while (!openSet.isEmpty()) {

            // En düşük fCost'lu hücre
            Cell current = openSet.pollFirst();
            Position currentPos = current.getPosition();
            openMap.remove(currentPos);

            if (currentPos.equals(end)) {
                long endTime = System.currentTimeMillis();
                Path path = PathUtils.reconstructPath(current);
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

                    // Eğer openSet içindeyse eski halini çıkar
                    if (openMap.containsKey(np)) {
                        openSet.remove(neighbor);
                    }

                    neighbor.setParent(current);
                    neighbor.setGCost(tentativeG);
                    neighbor.setHCost(heuristic(np, end));

                    openSet.add(neighbor);
                    openMap.put(np, neighbor);
                }
            }
        }

        long endTime = System.currentTimeMillis();
        return new Path(new ArrayList<>(), 0, endTime - startTime);
    }

    @Override
    public List<AlgorithmStep> findPathWithSteps(
            Maze maze, Position start, Position end) {

        List<AlgorithmStep> steps = new ArrayList<>();

        if (maze == null || start == null || end == null) {
            return steps;
        }

        maze.resetCells();

        // Open Set (Red-Black Tree)
        TreeSet<Cell> openSet = new TreeSet<>(cellComparator);
        Map<Position, Cell> openMap = new HashMap<>();

        // Closed Set
        Set<Position> closedSet = new HashSet<>();

        Cell startCell = maze.getCell(start);
        startCell.setGCost(0);
        startCell.setHCost(heuristic(start, end));
        startCell.setParent(null);

        openSet.add(startCell);
        openMap.put(start, startCell);

        steps.add(new AlgorithmStep(
                AlgorithmStep.StepType.VISIT,
                start,
                List.of(start),
                "A* started at " + start +
                        " (h=" + startCell.getHCost() + ")"
        ));

        int stepCount = 0;

        while (!openSet.isEmpty()) {

            // En düşük fCost'lu hücre
            Cell current = openSet.pollFirst();
            Position currentPos = current.getPosition();
            openMap.remove(currentPos);

            steps.add(new AlgorithmStep(
                    AlgorithmStep.StepType.EXPLORE,
                    currentPos,
                    new ArrayList<>(),
                    String.format(
                            "Step %d: Exploring %s (g=%d, h=%d, f=%d)",
                            ++stepCount,
                            currentPos,
                            current.getGCost(),
                            current.getHCost(),
                            current.getFCost()
                    )
            ));

            // Goal bulundu
            if (currentPos.equals(end)) {
                Path path = PathUtils.reconstructPath(current);
                steps.add(new AlgorithmStep(
                        AlgorithmStep.StepType.COMPLETE,
                        end,
                        path.getPositions(),
                        "Path found! Length: " +
                                path.getLength() +
                                ", Cost: " + current.getGCost()
                ));
                return steps;
            }

            closedSet.add(currentPos);

            for (Cell neighbor : maze.getNeighbors(currentPos)) {

                Position np = neighbor.getPosition();

                if (!neighbor.isWalkable() || closedSet.contains(np)) {
                    continue;
                }

                int tentativeG = current.getGCost() + 1;

                if (tentativeG < neighbor.getGCost()) {

                    // Eğer openSet'te varsa eski kaydı çıkar
                    if (openMap.containsKey(np)) {
                        openSet.remove(neighbor);
                    }

                    neighbor.setParent(current);
                    neighbor.setGCost(tentativeG);
                    neighbor.setHCost(heuristic(np, end));

                    openSet.add(neighbor);
                    openMap.put(np, neighbor);

                    steps.add(new AlgorithmStep(
                            AlgorithmStep.StepType.VISIT,
                            np,
                            new ArrayList<>(),
                            String.format(
                                    "Added to open set: %s (g=%d, h=%d, f=%d)",
                                    np,
                                    neighbor.getGCost(),
                                    neighbor.getHCost(),
                                    neighbor.getFCost()
                            )
                    ));
                }
            }
        }

        // Yol bulunamadı
        steps.add(new AlgorithmStep(
                AlgorithmStep.StepType.COMPLETE,
                end,
                new ArrayList<>(),
                "No path found!"
        ));

        return steps;
    }


    @Override
    public Path findPathMultiTarget(
            Maze maze,
            Position start,
            List<Position> targets) {

        if (maze == null || start == null ||
                targets == null || targets.isEmpty()) {
            return new Path(new ArrayList<>());
        }

        List<Position> completePath = new ArrayList<>();
        completePath.add(start);

        Position current = start;
        Set<Position> visited = new HashSet<>();

        // Her adımda en yakın hedefe git (greedy)
        while (visited.size() < targets.size()) {

            Position nearest = null;
            int minHeuristic = Integer.MAX_VALUE;

            for (Position target : targets) {
                if (visited.contains(target)) {
                    continue;
                }

                int h = heuristic(current, target);
                if (h < minHeuristic) {
                    minHeuristic = h;
                    nearest = target;
                }
            }

            // Gidilecek hedef kalmadı
            if (nearest == null) {
                break;
            }

            // TreeSet tabanlı A* çağrısı
            Path segment = findPath(maze, current, nearest);

            // Yol yoksa tüm çözüm başarısız
            if (segment.isEmpty()) {
                return new Path(new ArrayList<>());
            }

            // İlk pozisyonu (current) tekrar eklememek için subList
            List<Position> segmentPositions = segment.getPositions();
            completePath.addAll(
                    segmentPositions.subList(1, segmentPositions.size())
            );

            current = nearest;
            visited.add(nearest);
        }

        return new Path(completePath);
    }



    @Override
    public String getAlgorithmName() {
        return "A* Search";
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
