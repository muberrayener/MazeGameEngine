package com.maze.core;

import com.maze.model.Maze;
import com.maze.model.Path;
import com.maze.model.Position;
import com.maze.model.AlgorithmStep;
import java.util.List;

/**
 * Yol bulma algoritmaları için.
 * Strategy Pattern kullanılır.
 */
public interface IPathFinder {
    /**
     * Başlangıç ve hedef arasında yol bulur
     * @param maze Labirent
     * @param start Başlangıç pozisyonu
     * @param end Hedef pozisyonu
     * @return Bulunan yol (veya null)
     */
    Path findPath(Maze maze, Position start, Position end);

    /**
     * Adım adım yol bulur (animasyon için)
     * @param maze Labirent
     * @param start Başlangıç pozisyonu
     * @param end Hedef pozisyonu
     * @return Algoritma adımları listesi
     */
    List<AlgorithmStep> findPathWithSteps(Maze maze, Position start, Position end);

    /**
     * Çoklu hedef için optimum yol bulur
     * @param maze Labirent
     * @param start Başlangıç pozisyonu
     * @param targets Hedef pozisyonlar listesi
     * @return Tüm hedefleri kapsayan yol
     */
    Path findPathMultiTarget(Maze maze, Position start, List<Position> targets);

    /**
     * Algoritmanın adını döndürür
     * @return Algoritma adı (örn: "BFS", "A*")
     */
    String getAlgorithmName();

    /**
     * Algoritmanın optimal olup olmadığını belirtir
     * @return true ise her zaman en kısa yolu bulur
     */
    boolean isOptimal();

    /**
     * Algoritmanın time complexity'sini döndürür
     * @return Big-O notasyonu
     */
    String getTimeComplexity();

    /**
     * Algoritmanın space complexity'sini döndürür
     * @return Big-O notasyonu
     */
    String getSpaceComplexity();
}
