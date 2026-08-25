import java.util.HashMap;
import java.util.Map;

/**
 * Structural Design Patterns: Proxy Pattern
 * 
 * Core Concept: Provides a surrogate or placeholder for another object
 * to control, optimize, or restrict access to it.
 */

// =========================================================================
// 1. SUBJECT INTERFACE
// =========================================================================

interface VideoDownloader {
    String downloadVideo(String videoUrl);
}

// =========================================================================
// 2. REAL SUBJECT (Heavy Resource / Network I/O)
// =========================================================================

class RealVideoDownloader implements VideoDownloader {
    @Override
    public String downloadVideo(String videoUrl) {
        // Simulating expensive network latency
        System.out.println("🌐 [Network I/O] Connecting to origin server to download: " + videoUrl);
        try {
            Thread.sleep(100); // Simulated delay
        } catch (InterruptedException ignored) {}

        String content = "HD-Stream-Payload[" + videoUrl + "]";
        System.out.println("✅ [Network I/O] Download completed successfully.");
        return content;
    }
}

// =========================================================================
// 3. PROXY CLASS (Smart Caching Proxy)
// =========================================================================

class CachedVideoDownloader implements VideoDownloader {
    private final RealVideoDownloader realDownloader;
    private final Map<String, String> cache;

    public CachedVideoDownloader() {
        this.realDownloader = new RealVideoDownloader();
        this.cache = new HashMap<>();
    }

    @Override
    public String downloadVideo(String videoUrl) {
        // 1. Check if the requested resource already exists in cache
        if (cache.containsKey(videoUrl)) {
            System.out.println("⚡ [Cache HIT] Serving cached video payload instantly for: " + videoUrl);
            return cache.get(videoUrl);
        }

        // 2. Cache Miss: Delegate to Real Subject and store result
        System.out.println("⏳ [Cache MISS] Resource not in cache. Fetching from origin server...");
        String videoPayload = realDownloader.downloadVideo(videoUrl);
        cache.put(videoUrl, videoPayload);
        return videoPayload;
    }
}

// =========================================================================
// 🚀 Main Driver Program
// =========================================================================
public class ProxyPatternExample {
    public static void main(String[] args) {
        System.out.println("=== 🎥 Video Streaming Service with Caching Proxy ===");
        VideoDownloader proxyDownloader = new CachedVideoDownloader();

        // Request 1: First time downloading video A (Cache Miss -> Network Fetch)
        System.out.println("\n--- User 1 requests 'system-design-intro.mp4' ---");
        String video1 = proxyDownloader.downloadVideo("https://stream.tuf.com/system-design-intro.mp4");
        System.out.println("Received: " + video1);

        // Request 2: Second user requests the EXACT SAME video (Cache Hit -> Instant Response)
        System.out.println("\n--- User 2 requests 'system-design-intro.mp4' ---");
        String video2 = proxyDownloader.downloadVideo("https://stream.tuf.com/system-design-intro.mp4");
        System.out.println("Received: " + video2);

        // Request 3: User requests a different video B (Cache Miss -> Network Fetch)
        System.out.println("\n--- User 3 requests 'solid-principles-deep-dive.mp4' ---");
        String video3 = proxyDownloader.downloadVideo("https://stream.tuf.com/solid-principles-deep-dive.mp4");
        System.out.println("Received: " + video3);

        // Request 4: User 1 re-requests video B (Cache Hit -> Instant Response)
        System.out.println("\n--- User 1 re-requests 'solid-principles-deep-dive.mp4' ---");
        String video4 = proxyDownloader.downloadVideo("https://stream.tuf.com/solid-principles-deep-dive.mp4");
        System.out.println("Received: " + video4);
    }
}
