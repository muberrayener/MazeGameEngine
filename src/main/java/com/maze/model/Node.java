package com.maze.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Node {
    private final Position position;
    private final List<Node> neighbors;
    private int distance;  // Dijkstra için
    private Node previous;  // Yol takibi için

    public Node(Position position) {
        this.position = position;
        this.neighbors = new ArrayList<>();
        this.distance = Integer.MAX_VALUE;
        this.previous = null;
    }

    public void addNeighbor(Node neighbor) {
        if (!neighbors.contains(neighbor)) {
            neighbors.add(neighbor);
        }
    }

    public void removeNeighbor(Node neighbor) {
        neighbors.remove(neighbor);
    }

    public Position getPosition() { return position; }
    public List<Node> getNeighbors() { return new ArrayList<>(neighbors); }
    public int getDistance() { return distance; }
    public void setDistance(int distance) { this.distance = distance; }
    public Node getPrevious() { return previous; }
    public void setPrevious(Node previous) { this.previous = previous; }

    public void reset() {
        this.distance = Integer.MAX_VALUE;
        this.previous = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(position, node.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position);
    }

    @Override
    public String toString() {
        return "Node{" + position + ", neighbors=" + neighbors.size() + "}";
    }
}
