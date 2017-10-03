package com.github.marschall.maps;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A {@link Map} backed by a {@link ReadWriteLock}.
 *
 * <p>Unlike {@link java.util.Collections#synchronizedMap(Map)} allows
 * concurrent reads while using less memory than {@link ConcurrentHashMap}.</p>
 *
 * <p>As this map just wraps another map it will have the characteristics
 * of the wrapped map.</p>
 *
 * <p>Users have manually synchronize on this  map when iterating over
 * any of its collection views:</p>
 * <pre><code>
 *  Map map = new ReadWriteLockMap();
 *      ...
 *  Set set = map.keySet();
 *  ...
 *  map.readLock().lock();
 *  try {
 *    for (Object each : map.keySet()) {
 *      aMethod(each);
 *    }
 *  } finally {
 *    map.readLock().unlock();
 *  }
 * </code></pre>
 *
 * <p>Failure to follow this advice may result in non-deterministic behavior.</p>
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public final class ReadWriteLockMap<K, V> implements Map<K, V>, Serializable {

  private static final long serialVersionUID = 1L;

  private final ReadWriteLock lock;
  private final Map<K, V> map;

  /**
   * Initialize this wrapper around an existing map.
   *
   * @param map the map to wrap
   */
  public ReadWriteLockMap(Map<K, V> map) {
    this.map = map;
    this.lock = new ReentrantReadWriteLock();
  }

  /**
   * Initialize this wrapper around a {@link HashMap}.
   */
  public ReadWriteLockMap() {
    this(new HashMap<>());
  }

  /**
   * Access to the underlying read lock for protecting iteration.
   *
   * @return the underlying read lock.
   * @see #keySet()
   * @see #values()
   * @see #entrySet()
   */
  public Lock readLock() {
    return this.lock.readLock();
  }

  /**
   * Access to the underlying read lock for modification during iteration.
   *
   * @return the underlying write lock.
   * @see Iterator#remove()
   */
  public Lock writeLock() {
    return this.lock.writeLock();
  }

  @Override
  public int size() {
    Lock readLock = this.readLock();
    readLock.lock();
    try {
      return this.map.size();
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public boolean isEmpty() {
    Lock readLock = this.readLock();
    readLock.lock();
    try {
      return this.map.isEmpty();
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public boolean containsKey(Object key) {
    Lock readLock = this.readLock();
    readLock.lock();
    try {
      return this.map.containsKey(key);
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public boolean containsValue(Object value) {
    Lock readLock = this.readLock();
    readLock.lock();
    try {
      return this.map.containsValue(value);
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public V get(Object key) {
    Lock readLock = this.readLock();
    readLock.lock();
    try {
      return this.map.get(key);
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public V put(K key, V value) {
    Lock writeLock = this.writeLock();
    writeLock.lock();
    try {
      return this.map.put(key, value);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public V remove(Object key) {
    Lock writeLock = this.writeLock();
    writeLock.lock();
    try {
      return this.map.remove(key);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    Lock writeLock = this.writeLock();
    writeLock.lock();
    try {
      this.map.putAll(m);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public void clear() {
    Lock writeLock = this.writeLock();
    writeLock.lock();
    try {
      this.map.clear();
    } finally {
      writeLock.unlock();
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>The caller is responsible for synchronization using {@link #readLock()}
   * or {@link #writeLock()}.</p>
   */
  @Override
  public Set<K> keySet() {
    return this.map.keySet();
  }

  /**
   * {@inheritDoc}
   *
   * <p>The caller is responsible for synchronization using {@link #readLock()}
   * or {@link #writeLock()}.</p>
   */
  @Override
  public Collection<V> values() {
    return this.map.values();
  }

  /**
   * {@inheritDoc}
   *
   * <p>The caller is responsible for synchronization using {@link #readLock()}
   * or {@link #writeLock()}.</p>
   */
  @Override
  public Set<Entry<K, V>> entrySet() {
    return this.map.entrySet();
  }

  @Override
  public V getOrDefault(Object key, V defaultValue) {
    Lock readLock = this.readLock();
    readLock.lock();
    try {
      return this.map.getOrDefault(key, defaultValue);
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public void forEach(BiConsumer<? super K, ? super V> action) {
    Lock readLock = this.readLock();
    readLock.lock();
    try {
      this.map.forEach(action);
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
    Lock writeLock = this.writeLock();
    writeLock.lock();
    try {
      this.map.replaceAll(function);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public V putIfAbsent(K key, V value) {
    Lock writeLock = this.writeLock();
    writeLock.lock();
    try {
      return this.map.putIfAbsent(key, value);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public boolean remove(Object key, Object value) {
    Lock writeLock = this.writeLock();
    writeLock.lock();
    try {
      return this.map.remove(key, value);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public boolean replace(K key, V oldValue, V newValue) {
    Lock writeLock = this.writeLock();
    writeLock.lock();
    try {
      return this.map.replace(key, oldValue, newValue);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public V replace(K key, V value) {
    Lock writeLock = this.writeLock();
    writeLock.lock();
    try {
      return this.map.replace(key, value);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
    Lock writeLock = this.writeLock();
    writeLock.lock();
    try {
      return this.map.computeIfAbsent(key, mappingFunction);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    Lock writeLock = this.writeLock();
    writeLock.lock();
    try {
      return this.map.computeIfPresent(key, remappingFunction);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    Lock writeLock = this.writeLock();
    writeLock.lock();
    try {
      return this.map.compute(key, remappingFunction);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    Lock writeLock = this.writeLock();
    writeLock.lock();
    try {
      return this.map.merge(key, value, remappingFunction);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public int hashCode() {
    Lock readLock = this.readLock();
    readLock.lock();
    try {
      return this.map.hashCode();
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public boolean equals(Object obj) {
    Lock readLock = this.readLock();
    readLock.lock();
    try {
      return this.map.equals(obj);
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public String toString() {
    Lock readLock = this.readLock();
    readLock.lock();
    try {
      return this.map.toString();
    } finally {
      readLock.unlock();
    }
  }

  private void writeObject(ObjectOutputStream stream) throws IOException {
    Lock readLock = this.readLock();
    readLock.lock();
    try {
      stream.defaultWriteObject();
    } finally {
      readLock.unlock();
    }
  }

}
