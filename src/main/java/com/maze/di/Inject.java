package com.maze.di;

import java.lang.annotation.*;

/**
 * Constructor injection için annotation.
 * Constructor'a işaretlenerek DI container'a bilgi verir.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface Inject {
}