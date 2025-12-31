package com.maze.di;

import java.lang.annotation.*;

/**
 * Singleton scope için annotation.
 * Sınıfa işaretlenerek her zaman aynı instance döner.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Singleton {
}