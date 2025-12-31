package com.maze.di;

/**
 * DI Module interface.
 * Modüler konfigürasyon için.
 */
public interface Module {
    /**
     * Container'a binding'leri ekler
     */
    void configure(Container container);
}