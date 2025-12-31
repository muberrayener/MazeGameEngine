package com.maze.di;

import com.maze.core.*;
import com.maze.generator.*;
import com.maze.pathfinder.*;
import com.maze.service.*;

/**
 * Default DI modülü.
 * Standart binding'leri içerir.
 */
public class DefaultModule implements Module {

    @Override
    public void configure(Container container) {
        // Core interfaces
        container.bind(IMazeGenerator.class, KruskalMazeGenerator.class);
        container.bind(IPathFinder.class, AStarPathFinder.class);
        container.bind(IObstacleManager.class, ObstacleManager.class);

        // Services (Singleton)
        container.bindSingleton(GameEngine.class, GameEngine.class);
        container.bindSingleton(MazeService.class, MazeService.class);
    }
}