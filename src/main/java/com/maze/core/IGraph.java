package com.maze.core;

import com.maze.model.Node;
import com.maze.model.Edge;
import com.maze.model.Position;
import java.util.List;
import java.util.Set;

/**
 * Graf yapısı için.
 * Maze'i graf olarak temsil etmek için kullanılır.
 */
public interface IGraph {

    /**
     * Graf'a düğüm ekler
     * @param node Eklenecek düğüm
     */
    void addNode(Node node);

    /**
     * Graf'tan düğüm kaldırır
     * @param position Kaldırılacak düğümün pozisyonu
     */
    void removeNode(Position position);

    /**
     * Graf'a kenar ekler
     * @param edge Eklenecek kenar
     */
    void addEdge(Edge edge);

    /**
     * Graf'tan kenar kaldırır
     * @param from Başlangıç pozisyonu
     * @param to Hedef pozisyonu
     */
    void removeEdge(Position from, Position to);

    /**
     * Belirtilen pozisyondaki düğümü döndürür
     * @param position Pozisyon
     * @return Düğüm (veya null)
     */
    Node getNode(Position position);

    /**
     * Tüm düğümleri döndürür
     * @return Düğüm seti
     */
    Set<Node> getAllNodes();

    /**
     * Tüm kenarları döndürür
     * @return Kenar listesi
     */
    List<Edge> getAllEdges();

    /**
     * Bir düğümün komşularını döndürür
     * @param position Düğümün pozisyonu
     * @return Komşu düğümler listesi
     */
    List<Node> getNeighbors(Position position);

    /**
     * İki düğüm arasında kenar olup olmadığını kontrol eder
     * @param from Başlangıç pozisyonu
     * @param to Hedef pozisyonu
     * @return true ise kenar var
     */
    boolean hasEdge(Position from, Position to);

    /**
     * Graf'ı temizler (tüm düğüm ve kenarları siler)
     */
    void clear();

    /**
     * Düğüm sayısını döndürür
     * @return Düğüm sayısı
     */
    int getNodeCount();

    /**
     * Kenar sayısını döndürür
     * @return Kenar sayısı
     */
    int getEdgeCount();
}
