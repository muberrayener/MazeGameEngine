package com.maze.di;

import com.maze.core.*;
import com.maze.generator.*;
import com.maze.pathfinder.*;
import com.maze.service.*;

/**
 * Servis sağlayıcı - DI konfigürasyonu.
 * Tüm binding'leri merkezi olarak yönetir.
 */
public class ServiceProvider {

    private final Container container;

    public ServiceProvider() {
        this.container = new Container();
        configureServices();
    }

    /**
     * Servisleri konfigüre eder
     */
    private void configureServices() {
        // Generator bindings (Strategy Pattern)
        container.bind(IMazeGenerator.class, KruskalMazeGenerator.class);

        // PathFinder bindings (Strategy Pattern)
        container.bind(IPathFinder.class, BFSPathFinder.class);

        // ObstacleManager binding
        container.bind(IObstacleManager.class, ObstacleManager.class);

        // Services (Singleton olarak)
        container.bindSingleton(GameEngine.class, GameEngine.class);
        container.bindSingleton(MazeService.class, MazeService.class);
    }

    /**
     * GameEngine instance döndürür (DI ile)
     */
    public GameEngine getGameEngine() {
        return container.resolve(GameEngine.class);
    }

    /**
     * MazeService instance döndürür
     */
    public MazeService getMazeService() {
        return container.resolve(MazeService.class);
    }

    /**
     * Belirli bir generator ile GameEngine oluşturur
     */
    public GameEngine getGameEngineWithGenerator(GeneratorType type) {
        IMazeGenerator generator = createGenerator(type);
        IPathFinder pathFinder = container.resolve(IPathFinder.class);
        IObstacleManager obstacleManager = container.resolve(IObstacleManager.class);

        return new GameEngine(generator, pathFinder, obstacleManager);
    }

    /**
     * Belirli bir pathfinder ile GameEngine oluşturur
     */
    public GameEngine getGameEngineWithPathFinder(PathFinderType type) {
        IMazeGenerator generator = container.resolve(IMazeGenerator.class);
        IPathFinder pathFinder = createPathFinder(type);
        IObstacleManager obstacleManager = container.resolve(IObstacleManager.class);

        return new GameEngine(generator, pathFinder, obstacleManager);
    }

    /**
     * Özel konfigürasyon ile GameEngine oluşturur
     */
    public GameEngine getGameEngine(GeneratorType genType, PathFinderType pathType) {
        IMazeGenerator generator = createGenerator(genType);
        IPathFinder pathFinder = createPathFinder(pathType);
        IObstacleManager obstacleManager = container.resolve(IObstacleManager.class);

        return new GameEngine(generator, pathFinder, obstacleManager);
    }

    /**
     * Generator factory
     */
    private IMazeGenerator createGenerator(GeneratorType type) {
        switch (type) {
            case KRUSKAL:
                return new KruskalMazeGenerator();
            case PRIM:
                return new PrimMazeGenerator();
            case RECURSIVE_BACKTRACKER:
                return new RecursiveBacktracker();
            default:
                return new KruskalMazeGenerator();
        }
    }

    /**
     * PathFinder factory
     */
    private IPathFinder createPathFinder(PathFinderType type) {
        switch (type) {
            case BFS:
                return new BFSPathFinder();
            case DFS:
                return new DFSPathFinder();
            case ASTAR:
                return new AStarPathFinder();
            default:
                return new AStarPathFinder();
        }
    }

    /**
     * Container'a erişim (advanced usage)
     */
    public Container getContainer() {
        return container;
    }

    /**
     * Custom binding ekle
     */
    public <T> void registerService(Class<T> interfaceClass, Class<? extends T> implementationClass) {
        container.bind(interfaceClass, implementationClass);
    }

    /**
     * Custom instance kaydet
     */
    public <T> void registerInstance(Class<T> clazz, T instance) {
        container.bindInstance(clazz, instance);
    }

    // Enums
    public enum GeneratorType {
        KRUSKAL,
        PRIM,
        RECURSIVE_BACKTRACKER
    }

    public enum PathFinderType {
        BFS,
        DFS,
        ASTAR
    }
}