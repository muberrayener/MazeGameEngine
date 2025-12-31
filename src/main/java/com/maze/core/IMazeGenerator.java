package com.maze.core;
import com.maze.model.Maze;
import com.maze.model.AlgorithmStep;
import java.util.List;
/**
 * Labirent üretim algoritmaları için interface.
 * Strategy Pattern kullanılır.
 */
public interface IMazeGenerator {
    /**
     * Labirent üretir
     * @param rows Satır sayısı
     * @param cols Sütun sayısı
     * @return Üretilen labirent
     */
    Maze generate(int rows, int cols);

    /**
     * Adım adım labirent üretir (animasyon için)
     * @param rows Satır sayısı
     * @param cols Sütun sayısı
     * @return Algoritma adımları listesi
     */
    List<AlgorithmStep> generateWithSteps(int rows, int cols);

    /**
     * Algoritmanın adını döndürür
     * @return Algoritma adı (örn: "Kruskal", "Prim")
     */
    String getAlgorithmName();

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
