/**
 * Structural Design Patterns: Bridge Pattern
 * 
 * Core Concept: Decouples an abstraction (high-level platform logic)
 * from its implementation (low-level quality/codec rendering) so both can vary independently.
 * 
 * Reduces combinatorial class explosion from M * N to M + N.
 */

// =========================================================================
// 1. IMPLEMENTOR HIERARCHY (The Implementation dimension)
// =========================================================================

interface VideoQuality {
    void load(String title);
}

class SDQuality implements VideoQuality {
    @Override
    public void load(String title) {
        System.out.println("Streaming '" + title + "' in 480p SD Quality (Data Saver).");
    }
}

class HDQuality implements VideoQuality {
    @Override
    public void load(String title) {
        System.out.println("Streaming '" + title + "' in 1080p Full HD (60 FPS).");
    }
}

class UltraHDQuality implements VideoQuality {
    @Override
    public void load(String title) {
        System.out.println("Streaming '" + title + "' in 4K Ultra HD (Dolby Vision / HDR).");
    }
}

// =========================================================================
// 2. ABSTRACTION HIERARCHY (The Platform dimension with Bridge Reference)
// =========================================================================

abstract class VideoPlayer {
    // The Bridge: Reference to the Implementor interface
    protected VideoQuality quality;

    public VideoPlayer(VideoQuality quality) {
        this.quality = quality;
    }

    // Dynamic runtime switching
    public void setQuality(VideoQuality quality) {
        this.quality = quality;
    }

    public abstract void play(String title);
}

// =========================================================================
// 3. REFINED ABSTRACTIONS (Platform-specific implementations)
// =========================================================================

class WebPlayer extends VideoPlayer {
    public WebPlayer(VideoQuality quality) {
        super(quality);
    }

    @Override
    public void play(String title) {
        System.out.print("🌐 [Web Browser Player] ");
        quality.load(title);
    }
}

class MobilePlayer extends VideoPlayer {
    public MobilePlayer(VideoQuality quality) {
        super(quality);
    }

    @Override
    public void play(String title) {
        System.out.print("📱 [Mobile iOS/Android App] ");
        quality.load(title);
    }
}

class SmartTVPlayer extends VideoPlayer {
    public SmartTVPlayer(VideoQuality quality) {
        super(quality);
    }

    @Override
    public void play(String title) {
        System.out.print("📺 [Smart TV 65-inch Display] ");
        quality.load(title);
    }
}

// =========================================================================
// 🚀 Main Driver Program
// =========================================================================
public class BridgePatternExample {
    public static void main(String[] args) {
        System.out.println("=== 🎬 Video Streaming Platform with Bridge Pattern ===");

        // 1. Web Player streaming in HD Quality
        VideoPlayer webPlayer = new WebPlayer(new HDQuality());
        webPlayer.play("Interstellar (2014)");

        // 2. Smart TV streaming in 4K Ultra HD Quality
        VideoPlayer tvPlayer = new SmartTVPlayer(new UltraHDQuality());
        tvPlayer.play("Oppenheimer (2023)");

        // 3. Mobile Player starting in SD Quality on Cellular Data
        System.out.println("\n--- Mobile Streaming with Dynamic Quality Switching ---");
        VideoPlayer mobilePlayer = new MobilePlayer(new SDQuality());
        mobilePlayer.play("Stranger Things Season 5");

        // Wi-Fi connected: Dynamically upgrade quality without recreating player instance
        System.out.println("⚡ Wi-Fi detected! Upgrading to Ultra HD...");
        mobilePlayer.setQuality(new UltraHDQuality());
        mobilePlayer.play("Stranger Things Season 5");
    }
}
