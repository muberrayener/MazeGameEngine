package com.maze.model;

import java.util.Objects;

public class Edge implements Comparable<Edge> {
    private final Position from;
    private final Position to;
    private final double weight;

    public Edge(Position from, Position to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public Edge(Position from, Position to) {
        this(from, to, 1.0);
    }

    public Position getFrom() { return from; }
    public Position getTo() { return to; }
    public double getWeight() { return weight; }

    @Override
    public int compareTo(Edge other) {
        return Double.compare(this.weight, other.weight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(from, edge.from) &&
                Objects.equals(to, edge.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public String toString() {
        return "Edge{" + from + " -> " + to + ", w=" + weight + "}";
    }
}
