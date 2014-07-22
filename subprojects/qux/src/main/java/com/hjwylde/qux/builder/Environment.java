package com.hjwylde.qux.builder;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Optional;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class Environment<K, V> implements Iterable<Map.Entry<K, V>> {

    private final Environment<K, V> previous;

    private final Map<K, V> mapping;

    public Environment() {
        this(null, new HashMap<K, V>());
    }

    private Environment(Environment<K, V> previous, Map<K, V> mapping) {
        this.previous = previous;

        this.mapping = checkNotNull(mapping, "mapping cannot be null");
    }

    private Environment(Map<K, V> mapping) {
        this(null, mapping);
    }

    private Environment(Environment<K, V> previous) {
        this(previous, new HashMap<K, V>());
    }

    /**
     * Checks to see whether this environment contains the given key.
     *
     * @param key the key to check.
     * @return true if the key is contained.
     */
    public boolean contains(K key) {
        checkNotNull(key, "key cannot be null");

        return mapping.containsKey(key) || (previous != null && previous.contains(key));
    }

    public Set<Map.Entry<K, V>> entrySet() {
        return mapping.entrySet();
    }

    public Environment<K, V> flatten() {
        Map<K, V> flattened = new HashMap<>();
        for (Environment<K, V> env = this; env != null; env = env.previous) {
            Map<K, V> tmp = flattened;

            flattened = new HashMap<>(env.mapping);
            flattened.putAll(tmp);
        }

        return new Environment<>(flattened);
    }

    /**
     * Attempts to get the environment mapping for the given key. If the key does not exist, then
     * {@link com.google.common.base.Optional#absent()} is returned.
     *
     * @param key the key to get the value of.
     * @return the value or {@link com.google.common.base.Optional#absent()}.
     */
    public Optional<V> get(K key) {
        checkNotNull(key, "key cannot be null");

        Optional<V> value = Optional.fromNullable(mapping.get(key));

        return previous != null ? value.or(previous.get(key)) : value;
    }

    /**
     * Gets the key without any checks to determine whether the environment contains it. If the key
     * is not contained, then an exception is thrown.
     *
     * @param key the key to get the value of.
     * @return the value.
     * @throws IllegalStateException if the environment does not contain the key.
     */
    public V getUnchecked(K key) throws IllegalStateException {
        checkState(contains(key), "environment does not contain key '%s'", key);

        return get(key).get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return mapping.entrySet().iterator();
    }

    public Set<K> keySet() {
        return mapping.keySet();
    }

    public Environment<K, V> pop() {
        checkState(previous != null, "cannot pop final environment");

        return previous;
    }

    public Environment<K, V> push() {
        return new Environment<K, V>(this);
    }

    public void put(K key, V value) {
        checkNotNull(key, "key cannot be null");
        checkNotNull(value, "value cannot be null");

        mapping.put(key, value);
    }

    public void putAll(Map<K, V> mapping) {
        this.mapping.putAll(mapping);
    }

    public void putAll(Environment<K, V> env) {
        putAll(env.mapping);
    }

    public Optional<V> remove(K key) {
        return remove(key, false);
    }

    public Optional<V> remove(K key, boolean recurse) {
        if (contains(key)) {
            return Optional.of(mapping.remove(key));
        }

        if (recurse && previous != null) {
            return previous.remove(key, recurse);
        } else {
            return Optional.absent();
        }
    }

    public V removeUnchecked(K key) {
        return remove(key).get();
    }

    public V removeUnchecked(K key, boolean recurse) {
        return remove(key, recurse).get();
    }

    public String toString() {
        return "{mapping=" + mapping.toString() + ", previous=" + previous + "}";
    }

    public Collection<V> values() {
        return mapping.values();
    }
}
