package com._5icodes.starter.monitor.cache;

import com._5icodes.starter.monitor.cache.monitor.CacheContextUtils;
import com._5icodes.starter.monitor.cache.monitor.CacheMetricNodeRegister;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CustomValueOperations<K, V> implements ValueOperations<K, V> {
    private final ValueOperations<K, V> delegate;

    protected CustomValueOperations(ValueOperations<K, V> valueOperations) {
        delegate = valueOperations;
    }

    private <T> T execute(Supplier<T> supplier, Consumer<CacheContext> cacheContextConsumer) {
        CacheContext cacheContext = CacheContextUtils.getCacheContext();
        T res;
        if (null != cacheContext) {
            cacheContextConsumer.accept(cacheContext);
            res = supplier.get();
            CacheMetricNodeRegister.register(cacheContext);
        } else {
            res = supplier.get();
        }
        return res;
    }

    @Override
    public void set(K key, V value) {
        execute(() -> {
            delegate.set(key, value);
            return null;
        }, cacheContext -> cacheContext.setCacheOperationType(CacheOperationType.SET));
    }

    @Override
    public void set(K key, V value, long timeout, TimeUnit unit) {
        execute(() -> {
            delegate.set(key, value, timeout, unit);
            return null;
        }, cacheContext -> cacheContext.setCacheOperationType(CacheOperationType.SET));
    }

    @Override
    public Boolean setIfAbsent(K key, V value) {
        return execute(() -> delegate.setIfAbsent(key, value),
                cacheContext -> cacheContext.setCacheOperationType(CacheOperationType.SET));
    }

    @Override
    public Boolean setIfAbsent(K key, V value, long timeout, TimeUnit unit) {
        return execute(() -> delegate.setIfAbsent(key, value, timeout, unit),
                cacheContext -> cacheContext.setCacheOperationType(CacheOperationType.SET));
    }

    @Override
    public Boolean setIfPresent(K key, V value) {
        return execute(() -> delegate.setIfPresent(key, value),
                cacheContext -> cacheContext.setCacheOperationType(CacheOperationType.SET));
    }

    @Override
    public Boolean setIfPresent(K key, V value, long timeout, TimeUnit unit) {
        return execute(() -> delegate.setIfPresent(key, value, timeout, unit),
                cacheContext -> cacheContext.setCacheOperationType(CacheOperationType.SET));
    }

    @Override
    public void multiSet(Map<? extends K, ? extends V> map) {
        delegate.multiSet(map);
    }

    @Override
    public Boolean multiSetIfAbsent(Map<? extends K, ? extends V> map) {
        return delegate.multiSetIfAbsent(map);
    }

    @Override
    public V get(Object key) {
        return execute(() -> delegate.get(key),
                cacheContext -> cacheContext.setCacheOperationType(CacheOperationType.GET));
    }

    @Override
    public V getAndSet(K key, V value) {
        return delegate.getAndSet(key, value);
    }

    @Override
    public List<V> multiGet(Collection<K> keys) {
        return delegate.multiGet(keys);
    }

    @Override
    public Long increment(K key) {
        return delegate.increment(key);
    }

    @Override
    public Long increment(K key, long delta) {
        return delegate.increment(key, delta);
    }

    @Override
    public Double increment(K key, double delta) {
        return delegate.increment(key, delta);
    }

    @Override
    public Long decrement(K key) {
        return delegate.decrement(key);
    }

    @Override
    public Long decrement(K key, long delta) {
        return delegate.decrement(key, delta);
    }

    @Override
    public Integer append(K key, String value) {
        return delegate.append(key, value);
    }

    @Override
    public String get(K key, long start, long end) {
        return delegate.get(key, start, end);
    }

    @Override
    public void set(K key, V value, long offset) {
        delegate.set(key, value, offset);
    }

    @Override
    public Long size(K key) {
        return delegate.size(key);
    }

    @Override
    public Boolean setBit(K key, long offset, boolean value) {
        return delegate.setBit(key, offset, value);
    }

    @Override
    public Boolean getBit(K key, long offset) {
        return delegate.getBit(key, offset);
    }

    @Override
    public List<Long> bitField(K key, BitFieldSubCommands subCommands) {
        return delegate.bitField(key, subCommands);
    }

    @Override
    public RedisOperations<K, V> getOperations() {
        return delegate.getOperations();
    }
}