package com.elvenide.core.providers.map;

import com.elvenide.core.api.PublicAPI;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Represents a map with chainable methods in ElvenideCore.
 * @author <a href="https://elvenide.com">Elvenide</a>
 * @since 0.0.17
 */
@PublicAPI
public class CoreMap<K, V> extends LinkedHashMap<K, V> {

    /**
     * Creates an empty ElvenideCore map.
     */
    @PublicAPI
    public CoreMap() {
        super();
    }

    /**
     * Creates an ElvenideCore map from the given map.
     * @param map The map
     */
    @PublicAPI
    public CoreMap(Map<? extends K, ? extends V> map) {
        super(map);
    }

    /**
     * Adds an entry to the beginning of the map, and removes any existing entry not at the beginning.
     * <p>
     * It is recommended to use {@link #addFirst(Object, Object)} instead, for chainability.
     * @param k The key
     * @param v The value
     * @return The value
     */
    @Override
    public V putFirst(K k, V v) {
        return super.putFirst(k, v);
    }

    /**
     * Adds an entry to the end of the map, and removes any existing entry not at the end.
     * <p>
     * It is recommended to use {@link #addLast(Object, Object)} instead, for chainability.
     * @param k The key
     * @param v The value
     * @return The value
     */
    @Override
    public V putLast(K k, V v) {
        return super.putLast(k, v);
    }

    /**
     * Adds an entry to the end of the map, or updates an existing entry at its current position.
     * <p>
     * It is recommended to use {@link #add(Object, Object)} instead, for chainability.
     * @param key The key
     * @param value The value
     * @return The value
     */
    @Override
    public V put(K key, V value) {
        return super.put(key, value);
    }

    /**
     * Adds an entry to the end of the map, or updates an existing entry at its current position.
     * <p>
     * It is recommended to use {@link #addAll(Map)} instead, for chainability.
     * @param m The map of keys and values
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        super.putAll(m);
    }

    /**
     * Computes a value based on the key if it ISN'T already present in the map.
     * <p>
     * It is recommended to use {@link #addIfAbsent(Object, Function)} instead, for chainability.
     * @param key The key
     * @param mappingFunction The mapping function to compute a value
     * @return The new or existing value
     */
    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return super.computeIfAbsent(key, mappingFunction);
    }

    /**
     * Computes a value based on the key and current value if it IS present in the map.
     * <p>
     * It is recommended to use {@link #addIfPresent(Object, BiFunction)} instead, for chainability.
     * @param key The key
     * @param remappingFunction The remapping function to compute a value
     * @return The new or existing value
     */
    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return super.computeIfPresent(key, remappingFunction);
    }

    /**
     * Creates a shallow copy of this map.
     * Keys and values are NOT cloned; their direct references are added to the new map.
     * @return The cloned map
     */
    @SuppressWarnings("unchecked")
    @PublicAPI
    @Override
    public CoreMap<K, V> clone() {
        return (CoreMap<K, V>) super.clone();
    }

    /**
     * Adds an entry to the end of the map, or updates an existing entry at its current position.
     * @param key The key
     * @param value The value
     * @return The map
     */
    @PublicAPI
    public CoreMap<K, V> add(K key, V value) {
        this.put(key, value);
        return this;
    }

    /**
     * Adds an entry to the end of the map, or updates an existing entry at its current position.
     * @param map The map of keys and values
     * @return The map
     */
    @PublicAPI
    public CoreMap<K, V> addAll(Map<? extends K, ? extends V> map) {
        this.putAll(map);
        return this;
    }

    /**
     * Adds an entry to the beginning of the map, and removes any existing entry not at the beginning.
     * @param key The key
     * @param value The value
     * @return The map
     */
    @PublicAPI
    public CoreMap<K, V> addFirst(K key, V value) {
        this.putFirst(key, value);
        return this;
    }

    /**
     * Adds an entry to the end of the map, and removes any existing entry not at the end.
     * @param key The key
     * @param value The value
     * @return The map
     */
    @PublicAPI
    public CoreMap<K, V> addLast(K key, V value) {
        this.putLast(key, value);
        return this;
    }

    /**
     * Computes a value based on the key if it ISN'T already present in the map.
     * @param key The key
     * @param mappingFunction The mapping function to compute a value
     * @return The map
     */
    @PublicAPI
    public CoreMap<K, V> addIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        this.computeIfAbsent(key, mappingFunction);
        return this;
    }

    /**
     * Computes a value based on the key and current value if it IS present in the map.
     * @param key The key
     * @param remappingFunction The remapping function to compute a value
     * @return The map
     */
    @PublicAPI
    public CoreMap<K, V> addIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        this.computeIfPresent(key, remappingFunction);
        return this;
    }

}
