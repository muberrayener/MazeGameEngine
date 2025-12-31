package com.maze.model;

public class Cell {
    public enum Type {
        WALL,
        PATH,
        START,
        END,
        VISITED,
        OBSTACLE
    }
    private final Position position;
    private Type type;
    private boolean visited;
    private int gCost;
    private int hCost;
    private Cell parent;

    public Cell(Position position, Type type) {
        this.position = position;
        this.type = type;
        this.visited = false;
        this.gCost = Integer.MAX_VALUE;;
        this.hCost = 0;
        this.parent = null;
    }

    public Cell(int x, int y, Type type) {
        this.position = new Position(x, y);
        this.type = type;
        this.visited = false;
        this.gCost = Integer.MAX_VALUE;
        this.hCost = 0;
        this.parent = null;
    }

    public Position getPosition() { return position; }
    public Type getType() { return type; }
    public boolean isVisited() { return visited; }
    public int getGCost() { return gCost; }
    public int getHCost() { return hCost; }
    public int getFCost() { return gCost + hCost; } // A* için f = g + h
    public Cell getParent() { return parent; }

    public void setType(Type type) { this.type = type; }
    public void setVisited(boolean visited) { this.visited = visited; }
    public void setGCost(int gCost) { this.gCost = gCost; }
    public void setHCost(int hCost) { this.hCost = hCost; }
    public void setParent(Cell parent) { this.parent = parent; }

    public boolean isWalkable() {
        return type != Type.WALL && type != Type.OBSTACLE;
    }

    public void reset() {
        this.visited = false;
        this.gCost = Integer.MAX_VALUE;
        this.hCost = 0;
        this.parent = null;
    }

    @Override
    public String toString() {
        return "Cell{pos=" + position + ", type=" + type + "}";
    }
}
