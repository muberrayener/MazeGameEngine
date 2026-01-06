package com.maze.gui.view;

import com.maze.model.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Özel Canvas - Maze çizimi.
 */
public class MazeCanvas extends Canvas {

    private Maze maze;
    private Path path;
    private GraphicsContext gc;

    // Renkler
    private static final Color COLOR_WALL = Color.rgb(44, 62, 80);
    private static final Color COLOR_PATH = Color.rgb(236, 240, 241);
    private static final Color COLOR_START = Color.rgb(46, 204, 113);
    private static final Color COLOR_END = Color.rgb(231, 76, 60);
    private static final Color COLOR_SOLUTION = Color.rgb(52, 152, 219);
    private static final Color COLOR_OBSTACLE = Color.rgb(241, 196, 15);
    private static final Color COLOR_GRID = Color.rgb(189, 195, 199);

    public MazeCanvas(double width, double height) {
        super(width, height);
        this.gc = getGraphicsContext2D();

        // Background
        gc.setFill(COLOR_PATH);
        gc.fillRect(0, 0, width, height);
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
        draw();
    }

    public void setPath(Path path) {
        this.path = path;
        draw();
    }

    public void clear() {
        this.maze = null;
        this.path = null;
        gc.setFill(COLOR_PATH);
        gc.fillRect(0, 0, getWidth(), getHeight());
    }

    private void draw() {
        if (maze == null) return;

        // Clear
        gc.setFill(COLOR_PATH);
        gc.fillRect(0, 0, getWidth(), getHeight());

        int rows = maze.getRows();
        int cols = maze.getCols();

        double cellWidth = getWidth() / cols;
        double cellHeight = getHeight() / rows;

        // Draw cells
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = maze.getCell(r, c);
                double x = c * cellWidth;
                double y = r * cellHeight;

                // Cell color
                if (cell.getType() == Cell.Type.WALL) {
                    gc.setFill(COLOR_WALL);
                } else if (cell.getType() == Cell.Type.START) {
                    gc.setFill(COLOR_START);
                } else if (cell.getType() == Cell.Type.END) {
                    gc.setFill(COLOR_END);
                } else if (cell.getType() == Cell.Type.OBSTACLE) {
                    gc.setFill(COLOR_OBSTACLE);
                } else {
                    gc.setFill(COLOR_PATH);
                }

                gc.fillRect(x, y, cellWidth, cellHeight);

                // Grid lines
                gc.setStroke(COLOR_GRID);
                gc.setLineWidth(0.5);
                gc.strokeRect(x, y, cellWidth, cellHeight);
            }
        }

        // Draw path
        if (path != null && !path.isEmpty()) {
            gc.setStroke(COLOR_SOLUTION);
            gc.setLineWidth(cellWidth * 0.3);

            var positions = path.getPositions();
            for (int i = 0; i < positions.size() - 1; i++) {
                Position current = positions.get(i);
                Position next = positions.get(i + 1);

                double x1 = (current.getCol() + 0.5) * cellWidth;
                double y1 = (current.getRow() + 0.5) * cellHeight;
                double x2 = (next.getCol() + 0.5) * cellWidth;
                double y2 = (next.getRow() + 0.5) * cellHeight;

                gc.strokeLine(x1, y1, x2, y2);
            }
        }
    }
}