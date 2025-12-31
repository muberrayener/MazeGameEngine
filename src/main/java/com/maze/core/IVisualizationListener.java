package com.maze.core;

import com.maze.model.AlgorithmStep;
import com.maze.model.Position;

/**
 * Görselleştirme için callback interface.
 * Observer Pattern kullanılır.
 */
public interface IVisualizationListener {

    /**
     * Algoritma bir adım ilerlediğinde çağrılır
     * @param step Algoritma adımı
     */
    void onStepExecuted(AlgorithmStep step);

    /**
     * Bir hücre ziyaret edildiğinde çağrılır
     * @param position Ziyaret edilen pozisyon
     */
    void onCellVisited(Position position);

    /**
     * Bir hücre keşfedildiğinde çağrılır
     * @param position Keşfedilen pozisyon
     */
    void onCellExplored(Position position);

    /**
     * Yol bulunduğunda çağrılır
     * @param pathLength Yol uzunluğu
     * @param visitedCount Ziyaret edilen hücre sayısı
     */
    void onPathFound(int pathLength, int visitedCount);

    /**
     * Yol bulunamadığında çağrılır
     */
    void onNoPathFound();

    /**
     * Algoritma başladığında çağrılır
     * @param algorithmName Algoritma adı
     */
    void onAlgorithmStarted(String algorithmName);

    /**
     * Algoritma tamamlandığında çağrılır
     * @param elapsedTimeMs Geçen süre (ms)
     */
    void onAlgorithmCompleted(long elapsedTimeMs);
}
