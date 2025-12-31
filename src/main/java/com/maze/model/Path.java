package com.maze.model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Path {
    private final List<Position> positions;
    private final double cost;
    private final int length;
    private final long computationTimeMs;

    public Path(List<Position> positions, double cost, long computationTimeMs) {
        this.positions = positions;
        this.cost = cost;
        this.length = positions.size();
        this.computationTimeMs = computationTimeMs;
    }

    public Path(List<Position> positions) {
        this(positions, positions.size(), 0);
    }

    public boolean isEmpty() {
        return positions.isEmpty();
    }

    public Position getStart() {
        return positions.isEmpty() ? null : positions.get(0);
    }

    public Position getEnd() {
        return positions.isEmpty() ? null : positions.get(positions.size() - 1);
    }

    public Position getPosition(int index) {
        return positions.get(index);
    }

    public boolean contains(Position pos) {
        return positions.contains(pos);
    }

    public List<Position> getPositions() {
        return Collections.unmodifiableList(positions);
    }

    public int getLength() {
        return length;
    }

    public double getCost() {
        return cost;
    }

    public long getComputationTimeMs() {
        return computationTimeMs;
    }

    @Override
    public String toString() {
        return "Path{length=" + length +
                ", cost=" + cost +
                ", time=" + computationTimeMs + "ms}";
    }
}
