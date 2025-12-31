package com.maze.util;

/**
 * Union-Find (Disjoint Set) veri yapısı.
 * Kruskal MST algoritması için kullanılır.
 *
 * Time Complexity:
 * - find: O(α(n)) ≈ O(1) amortized (path compression ile)
 * - union: O(α(n)) ≈ O(1) amortized (union by rank ile)
 */
public class UnionFind {
    private final int[] parent;
    private final int[] rank;
    private int componentCount;

    /**
     * Union-Find yapısını oluşturur
     * @param size Eleman sayısı
     */
    public UnionFind(int size) {
        this.parent = new int[size];
        this.rank = new int[size];
        this.componentCount = size;

        // Her eleman kendi parent'ı
        for (int i = 0; i < size; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    /**
     * Bir elemanın root'unu bulur (path compression ile)
     * @param x Eleman
     * @return Root
     */
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // Path compression
        }
        return parent[x];
    }

    /**
     * İki elemanı birleştirir (union by rank ile)
     * @param x İlk eleman
     * @param y İkinci eleman
     * @return true ise birleştirme yapıldı (aynı set değillerdi)
     */
    public boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);

        // Zaten aynı set'te
        if (rootX == rootY) {
            return false;
        }

        // Union by rank
        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }

        componentCount--;
        return true;
    }

    /**
     * İki eleman aynı set'te mi kontrol eder
     * @param x İlk eleman
     * @param y İkinci eleman
     * @return true ise aynı set'te
     */
    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }

    /**
     * Toplam set sayısını döndürür
     * @return Set sayısı
     */
    public int getComponentCount() {
        return componentCount;
    }

    /**
     * Yapıyı sıfırlar
     */
    public void reset() {
        for (int i = 0; i < parent.length; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
        componentCount = parent.length;
    }

    @Override
    public String toString() {
        return "UnionFind{components=" + componentCount + "}";
    }
}
