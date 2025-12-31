package com.maze.core;

import com.maze.model.Maze;
import com.maze.model.Position;
import java.util.List;

/**
 * Dinamik engel yönetimi için.
 * Runtime'da engel ekleme/çıkarma.
 */
public interface IObstacleManager {

    /**
     * Belirtilen pozisyona engel ekler
     * @param maze Labirent
     * @param position Engel pozisyonu
     * @return true ise başarıyla eklendi
     */
    boolean addObstacle(Maze maze, Position position);

    /**
     * Belirtilen pozisyondan engeli kaldırır
     * @param maze Labirent
     * @param position Engel pozisyonu
     * @return true ise başarıyla kaldırıldı
     */
    boolean removeObstacle(Maze maze, Position position);

    /**
     * Tüm engelleri kaldırır
     * @param maze Labirent
     */
    void clearObstacles(Maze maze);

    /**
     * Rastgele engeller ekler
     * @param maze Labirent
     * @param count Eklenecek engel sayısı
     * @return Eklenen engel pozisyonları
     */
    List<Position> addRandomObstacles(Maze maze, int count);

    /**
     * Aktif engellerin listesini döndürür
     * @param maze Labirent
     * @return Engel pozisyonları listesi
     */
    List<Position> getObstacles(Maze maze);

    /**
     * Belirtilen pozisyonun engel olup olmadığını kontrol eder
     * @param maze Labirent
     * @param position Kontrol edilecek pozisyon
     * @return true ise engel
     */
    boolean isObstacle(Maze maze, Position position);

    /**
     * Engel sayısını döndürür
     * @param maze Labirent
     * @return Engel sayısı
     */
    int getObstacleCount(Maze maze);
}