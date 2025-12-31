package com.maze.di;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dependency Injection Container.
 * IoC (Inversion of Control) pattern.
 *
 * Özellikler:
 * - Constructor injection
 * - Singleton support
 * - Interface to implementation mapping
 * - Automatic dependency resolution
 */
public class Container {

    // Interface -> Implementation mapping
    private final Map<Class<?>, Class<?>> bindings = new ConcurrentHashMap<>();

    // Singleton instances cache
    private final Map<Class<?>, Object> singletons = new ConcurrentHashMap<>();

    // Instance providers (factory functions)
    private final Map<Class<?>, InstanceProvider<?>> providers = new ConcurrentHashMap<>();

    /**
     * Interface'i implementation'a bağlar
     */
    public <T> void bind(Class<T> interfaceClass, Class<? extends T> implementationClass) {
        bindings.put(interfaceClass, implementationClass);
    }

    /**
     * Singleton olarak kaydeder
     */
    public <T> void bindSingleton(Class<T> interfaceClass, Class<? extends T> implementationClass) {
        bind(interfaceClass, implementationClass);
        // Singleton annotation ekle (runtime'da kontrol edilecek)
    }

    /**
     * Instance provider ile kaydeder (factory pattern)
     */
    public <T> void bindProvider(Class<T> clazz, InstanceProvider<T> provider) {
        providers.put(clazz, provider);
    }

    /**
     * Direct instance kaydeder
     */
    public <T> void bindInstance(Class<T> clazz, T instance) {
        singletons.put(clazz, instance);
    }

    /**
     * Instance resolve eder (ana metot)
     */
    @SuppressWarnings("unchecked")
    public <T> T resolve(Class<T> clazz) {
        try {
            // 1. Singleton cache'de var mı?
            if (singletons.containsKey(clazz)) {
                return (T) singletons.get(clazz);
            }

            // 2. Provider var mı?
            if (providers.containsKey(clazz)) {
                InstanceProvider<T> provider = (InstanceProvider<T>) providers.get(clazz);
                T instance = provider.provide();

                // Singleton ise cache'le
                if (isSingleton(clazz)) {
                    singletons.put(clazz, instance);
                }

                return instance;
            }

            // 3. Binding var mı? (interface -> implementation)
            Class<?> implementationClass = bindings.getOrDefault(clazz, clazz);

            // 4. Singleton mi kontrol et
            if (isSingleton(implementationClass)) {
                if (singletons.containsKey(implementationClass)) {
                    return (T) singletons.get(implementationClass);
                }

                T instance = createInstance(implementationClass);
                singletons.put(implementationClass, instance);
                return instance;
            }

            // 5. Yeni instance oluştur
            return (T) createInstance(implementationClass);

        } catch (Exception e) {
            throw new DependencyResolutionException(
                    "Failed to resolve " + clazz.getName(), e
            );
        }
    }

    /**
     * Instance oluşturur (reflection ile)
     */
    @SuppressWarnings("unchecked")
    private <T> T createInstance(Class<?> clazz) throws Exception {
        // @Inject annotation'lı constructor ara
        Constructor<?> injectConstructor = findInjectConstructor(clazz);

        if (injectConstructor != null) {
            return (T) createInstanceWithConstructor(injectConstructor);
        }

        // Default constructor dene
        Constructor<?>[] constructors = clazz.getConstructors();

        if (constructors.length == 0) {
            throw new DependencyResolutionException(
                    "No public constructor found for " + clazz.getName()
            );
        }

        // En fazla parametreli constructor'ı seç (greedy)
        Constructor<?> constructor = Arrays.stream(constructors)
                .max(Comparator.comparingInt(Constructor::getParameterCount))
                .orElseThrow();

        return (T) createInstanceWithConstructor(constructor);
    }

    /**
     * Constructor ile instance oluşturur
     */
    private Object createInstanceWithConstructor(Constructor<?> constructor) throws Exception {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] parameters = new Object[parameterTypes.length];

        // Her parametreyi recursive olarak resolve et
        for (int i = 0; i < parameterTypes.length; i++) {
            parameters[i] = resolve(parameterTypes[i]);
        }

        constructor.setAccessible(true);
        return constructor.newInstance(parameters);
    }

    /**
     * @Inject annotation'lı constructor bulur
     */
    private Constructor<?> findInjectConstructor(Class<?> clazz) {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.isAnnotationPresent(Inject.class)) {
                return constructor;
            }
        }
        return null;
    }

    /**
     * Singleton mi kontrol eder
     */
    private boolean isSingleton(Class<?> clazz) {
        return clazz.isAnnotationPresent(Singleton.class);
    }

    /**
     * Container'ı temizler
     */
    public void clear() {
        bindings.clear();
        singletons.clear();
        providers.clear();
    }

    /**
     * Kayıtlı binding sayısı
     */
    public int getBindingCount() {
        return bindings.size();
    }

    /**
     * Singleton sayısı
     */
    public int getSingletonCount() {
        return singletons.size();
    }

    // Inner interface
    @FunctionalInterface
    public interface InstanceProvider<T> {
        T provide();
    }

    // Custom exception
    public static class DependencyResolutionException extends RuntimeException {
        public DependencyResolutionException(String message) {
            super(message);
        }

        public DependencyResolutionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}