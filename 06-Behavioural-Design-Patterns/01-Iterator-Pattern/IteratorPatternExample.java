import java.util.ArrayList;
import java.util.List;

/**
 * Behavioural Design Patterns: Iterator Pattern
 * 
 * Core Concept: Provides a way to access elements of an aggregate collection
 * sequentially without exposing its underlying internal representation.
 */

// =========================================================================
// 1. DOMAIN ENTITY
// =========================================================================

class Video {
    private final String title;
    private final int durationInMinutes;

    public Video(String title, int durationInMinutes) {
        this.title = title;
        this.durationInMinutes = durationInMinutes;
    }

    public String getTitle() {
        return title;
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    @Override
    public String toString() {
        return "🎬 " + title + " (" + durationInMinutes + " mins)";
    }
}

// =========================================================================
// 2. ITERATOR INTERFACE (Traversal Contract)
// =========================================================================

interface PlaylistIterator {
    boolean hasNext();
    Video next();
}

// =========================================================================
// 3. AGGREGATE INTERFACE (Collection Contract)
// =========================================================================

interface Playlist {
    PlaylistIterator createIterator();
    PlaylistIterator createReverseIterator();
}

// =========================================================================
// 4. CONCRETE ITERATORS
// =========================================================================

// Forward Sequential Iterator
class YouTubePlaylistIterator implements PlaylistIterator {
    private final List<Video> videos;
    private int position = 0;

    public YouTubePlaylistIterator(List<Video> videos) {
        this.videos = videos;
    }

    @Override
    public boolean hasNext() {
        return position < videos.size();
    }

    @Override
    public Video next() {
        return hasNext() ? videos.get(position++) : null;
    }
}

// Reverse Sequential Iterator (Demonstrating multiple traversal strategies)
class ReversePlaylistIterator implements PlaylistIterator {
    private final List<Video> videos;
    private int position;

    public ReversePlaylistIterator(List<Video> videos) {
        this.videos = videos;
        this.position = videos.size() - 1;
    }

    @Override
    public boolean hasNext() {
        return position >= 0;
    }

    @Override
    public Video next() {
        return hasNext() ? videos.get(position--) : null;
    }
}

// =========================================================================
// 5. CONCRETE AGGREGATE
// =========================================================================

class YouTubePlaylist implements Playlist {
    // Encapsulated internal collection
    private final List<Video> videos = new ArrayList<>();

    public void addVideo(Video video) {
        videos.add(video);
    }

    @Override
    public PlaylistIterator createIterator() {
        return new YouTubePlaylistIterator(videos);
    }

    @Override
    public PlaylistIterator createReverseIterator() {
        return new ReversePlaylistIterator(videos);
    }
}

// =========================================================================
// 🚀 Main Driver Program
// =========================================================================
public class IteratorPatternExample {
    public static void main(String[] args) {
        System.out.println("=== 📺 YouTube Playlist with Iterator Pattern ===");

        YouTubePlaylist playlist = new YouTubePlaylist();
        playlist.addVideo(new Video("1. Introduction to Low-Level Design", 18));
        playlist.addVideo(new Video("2. SOLID Principles Deep Dive", 42));
        playlist.addVideo(new Video("3. Factory and Abstract Factory Patterns", 35));
        playlist.addVideo(new Video("4. Decorator Pattern with Pizza Toppings", 25));
        playlist.addVideo(new Video("5. Proxy Pattern & Caching", 20));

        // 1. Forward Traversal
        System.out.println("\n--- 1. Sequential Forward Playback ---");
        PlaylistIterator forwardIt = playlist.createIterator();
        while (forwardIt.hasNext()) {
            System.out.println(forwardIt.next());
        }

        // 2. Reverse Traversal
        System.out.println("\n--- 2. Reverse Playback (Latest to First) ---");
        PlaylistIterator reverseIt = playlist.createReverseIterator();
        while (reverseIt.hasNext()) {
            System.out.println(reverseIt.next());
        }

        // 3. Simultaneous Independent Iterations
        System.out.println("\n--- 3. Two Independent Users Iterating Simultaneously ---");
        PlaylistIterator user1 = playlist.createIterator();
        PlaylistIterator user2 = playlist.createIterator();

        System.out.println("User 1 plays: " + user1.next());
        System.out.println("User 1 plays: " + user1.next());
        System.out.println("User 2 plays: " + user2.next()); // User 2 is independently at video 1
        System.out.println("User 1 plays: " + user1.next());
    }
}
