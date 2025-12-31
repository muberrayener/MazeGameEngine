package com.maze.model;
import java.util.ArrayList;
import java.util.List;

public class Maze {
    private final int rows;
    private final int cols;
    private final Cell[][] grid;
    private Position startPosition;
    private Position endPosition;
    private final List<Position> obstacles;

    public Maze(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new Cell[rows][cols];
        this.obstacles = new ArrayList<>();
        initializeGrid();
    }

    private void initializeGrid() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new Cell(r, c, Cell.Type.WALL);
            }
        }
    }

    public Cell getCell(int row, int col) {
        if (isValid(row, col)) {
            return grid[row][col];
        }
        return null;
    }

    public Cell getCell(Position pos) {
        return getCell(pos.getRow(), pos.getCol());
    }

    public void setCell(int row, int col, Cell.Type type) {
        if (isValid(row, col)) {
            grid[row][col].setType(type);
        }
    }

    public boolean isValid(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public boolean isValid(Position pos) {
        return isValid(pos.getRow(), pos.getCol());
    }

    public List<Cell> getNeighbors(Position pos) {
        List<Cell> neighbors = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            int newRow = pos.getRow() + dir[0];
            int newCol = pos.getCol() + dir[1];
            if (isValid(newRow, newCol)) {
                neighbors.add(grid[newRow][newCol]);
            }
        }
        return neighbors;
    }

    public List<Cell> getNeighbors8(Position pos) {
        List<Cell> neighbors = new ArrayList<>();
        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},  // 4 yön
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1} // diagonal
        };

        for (int[] dir : directions) {
            int newRow = pos.getRow() + dir[0];
            int newCol = pos.getCol() + dir[1];
            if (isValid(newRow, newCol)) {
                neighbors.add(grid[newRow][newCol]);
            }
        }
        return neighbors;
    }

    public void resetCells() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c].reset();
            }
        }
    }

    public void addObstacle(Position pos) {
        if (isValid(pos) && !obstacles.contains(pos)) {
            obstacles.add(pos);
            getCell(pos).setType(Cell.Type.OBSTACLE);
        }
    }

    public void removeObstacle(Position pos) {
        obstacles.remove(pos);
        if (isValid(pos)) {
            getCell(pos).setType(Cell.Type.PATH);
        }
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public Cell[][] getGrid() { return grid; }
    public Position getStartPosition() { return startPosition; }
    public void setStartPosition(Position pos) { this.startPosition = pos; }
    public Position getEndPosition() { return endPosition; }
    public void setEndPosition(Position pos) { this.endPosition = pos; }
    public List<Position> getObstacles() { return new ArrayList<>(obstacles); }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell.Type type = grid[r][c].getType();
                sb.append(type == Cell.Type.WALL ? "█" : " ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
