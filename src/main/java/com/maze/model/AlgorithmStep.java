package com.maze.model;

import java.util.ArrayList;
import java.util.List;

public class AlgorithmStep {
    public enum StepType {
        VISIT,      // Hücre ziyaret edildi
        EXPLORE,    // Hücre keşfedildi
        PATH,       // Yola eklendi
        BACKTRACK,  // Geri dönüldü
        COMPLETE    // Algoritma tamamlandı
    }

    private final StepType type;
    private final Position position;
    private final List<Position> currentPath;
    private final String description;

    public AlgorithmStep(StepType type, Position position,
                         List<Position> currentPath, String description) {
        this.type = type;
        this.position = position;
        this.currentPath = new ArrayList<>(currentPath);
        this.description = description;
    }

    public AlgorithmStep(StepType type, Position position) {
        this(type, position, new ArrayList<>(), "");
    }

    public StepType getType() { return type; }
    public Position getPosition() { return position; }
    public List<Position> getCurrentPath() {
        return new ArrayList<>(currentPath);
    }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return "Step{" + type + " at " + position +
                ", path=" + currentPath.size() + "}";
    }
}
