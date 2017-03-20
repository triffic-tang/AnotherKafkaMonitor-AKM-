package cc.triffic.wc.kafkamonitor.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class LRUCacheUtils<K, V> {
	private final int MAX_CACHE_SIZE;
	LinkedHashMap<K, V> map;

	public LRUCacheUtils(int cacheSize) {
		this.MAX_CACHE_SIZE = cacheSize;

		int capacity = (int) Math.ceil(this.MAX_CACHE_SIZE / 0.75F) + 1;
		this.map = new LinkedHashMap<K, V>(capacity, 0.75F, true) {
			private static final long serialVersionUID = 1L;
			protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
				return (size() > LRUCacheUtils.this.MAX_CACHE_SIZE);
			}
		};
	}

	public synchronized void put(K key, V value) {
		this.map.put(key, value);
	}

	public synchronized V get(K key) {
		return this.map.get(key);
	}

	public synchronized void remove(K key) {
		this.map.remove(key);
	}

	public synchronized Set<Map.Entry<K, V>> getAll() {
		return this.map.entrySet();
	}

	public synchronized int size() {
		return this.map.size();
	}

	public synchronized void clear() {
		this.map.clear();
	}

	public synchronized boolean containsKey(K key) {
		return this.map.containsKey(key);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<K, V> entry : this.map.entrySet()) {
			sb.append(String.format("%s:%s ", new Object[] { entry.getKey(),
					entry.getValue() }));
		}
		return sb.toString();
	}
}