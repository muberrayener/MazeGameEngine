package com.maze.model;

public class GameState {
    public enum State {
        IDLE,           // Bekliyor
        GENERATING,     // Labirent üretiliyor
        GENERATED,      // Labirent hazır
        OBSTACLE_ADDED, // Engel eklendi
        SOLVING,        // Yol bulunuyor
        SOLVED,         // Yol bulundu
        NO_SOLUTION,    // Yol bulunamadı
        PAUSED          // Duraklatıldı
    }

    private State currentState;
    private Maze maze;
    private Path currentPath;
    private int visitedCells;
    private long elapsedTime;

    public GameState() {
        this.currentState = State.IDLE;
        this.visitedCells = 0;
        this.elapsedTime = 0;
    }

    public State getCurrentState() { return currentState; }
    public void setCurrentState(State state) { this.currentState = state; }
    public Maze getMaze() { return maze; }
    public void setMaze(Maze maze) { this.maze = maze; }
    public Path getCurrentPath() { return currentPath; }
    public void setCurrentPath(Path path) { this.currentPath = path; }
    public int getVisitedCells() { return visitedCells; }
    public void setVisitedCells(int count) { this.visitedCells = count; }
    public long getElapsedTime() { return elapsedTime; }
    public void setElapsedTime(long time) { this.elapsedTime = time; }

    public void reset() {
        this.currentState = State.IDLE;
        this.currentPath = null;
        this.visitedCells = 0;
        this.elapsedTime = 0;
    }

    @Override
    public String toString() {
        return "GameState{" +
                "state=" + currentState +
                ", visited=" + visitedCells +
                ", time=" + elapsedTime + "ms}";
    }
}
