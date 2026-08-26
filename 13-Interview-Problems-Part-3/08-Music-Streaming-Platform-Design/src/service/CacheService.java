package service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class CacheService {
    private final int maxEntries;
    private final Map<String, byte[]> lruCache;

    public CacheService() {
        this(100);
    }

    public CacheService(int maxEntries) {
        this.maxEntries = maxEntries;
        this.lruCache = new LinkedHashMap<String, byte[]>(maxEntries, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, byte[]> eldest) {
                return size() > maxEntries;
            }
        };
    }

    public synchronized Optional<byte[]> getChunk(String songId, long start, long end) {
        String key = buildKey(songId, start, end);
        byte[] chunk = lruCache.get(key);
        if (chunk != null) {
            System.out.println("⚡ [Cache HIT] Serving " + key + " from memory (" + chunk.length + " bytes)");
            return Optional.of(chunk);
        }
        System.out.println("💨 [Cache MISS] " + key + " not in cache.");
        return Optional.empty();
    }

    public synchronized void putChunk(String songId, long start, long end, byte[] chunk) {
        String key = buildKey(songId, start, end);
        lruCache.put(key, chunk);
        System.out.println("💾 [Cache PUT] Cached " + key + " (" + chunk.length + " bytes)");
    }

    public synchronized void evictSong(String songId) {
        lruCache.keySet().removeIf(k -> k.startsWith("chunk_" + songId + "_"));
        System.out.println("🧹 [Cache EVICT] Evicted all cached chunks for song: " + songId);
    }

    private String buildKey(String songId, long start, long end) {
        return "chunk_" + songId + "_" + start + "_" + end;
    }
}
