package main;

import controller.*;
import domain.*;
import repository.impl.*;
import service.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Driver simulation for the Music Streaming Platform (Spotify / Apple Music) LLD.
 * Demonstrates:
 * 1. Catalog provisioning: Artists, Albums, Songs across multiple genres
 * 2. User onboarding with Free vs Premium subscription tiers
 * 3. Unified search for songs, artists, albums, playlists
 * 4. Unified playback session management (Song, Album, Playlist queues)
 * 5. Chunk-based audio streaming (HTTP Range simulation) and LRU cache hits/misses
 * 6. Playback controls: Pause, Resume, Skip Next, Repeat, Shuffle, and Progress tracking
 * 7. Playlist CRUD operations with concurrent locking
 * 8. Offline downloads validation (Premium vs Free enforcement)
 * 9. Personalized song recommendations via Strategy Pattern
 */
public class MusicStreamingSimulation {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("🎧 MUSIC STREAMING PLATFORM (SPOTIFY) - LLD DEMO");
        System.out.println("==================================================");

        // 1. Repositories & Services Setup
        UserRepositoryImpl userRepo = new UserRepositoryImpl();
        ArtistRepositoryImpl artistRepo = new ArtistRepositoryImpl();
        AlbumRepositoryImpl albumRepo = new AlbumRepositoryImpl();
        SongRepositoryImpl songRepo = new SongRepositoryImpl();
        PlaylistRepositoryImpl playlistRepo = new PlaylistRepositoryImpl();
        PlaybackSessionRepositoryImpl sessionRepo = new PlaybackSessionRepositoryImpl();
        DownloadRepositoryImpl downloadRepo = new DownloadRepositoryImpl();
        ListeningHistoryRepositoryImpl historyRepo = new ListeningHistoryRepositoryImpl();

        LockService lockService = new LockService();
        CacheService cacheService = new CacheService(50);
        StreamingService streamingService = new StreamingService(songRepo, cacheService);
        DownloadService downloadService = new DownloadService(downloadRepo, userRepo, streamingService);
        SearchService searchService = new SearchService(songRepo, artistRepo, albumRepo, playlistRepo);
        RecommendationService recommendationService = new RecommendationService(historyRepo, songRepo);
        PlaylistService playlistService = new PlaylistService(playlistRepo, songRepo, lockService);
        PlaybackService playbackService = new PlaybackService(sessionRepo, userRepo, songRepo, albumRepo,
                playlistRepo, historyRepo, streamingService);

        PlaybackController playbackController = new PlaybackController(playbackService);
        SearchController searchController = new SearchController(searchService);
        PlaylistController playlistController = new PlaylistController(playlistService);
        StreamingController streamingController = new StreamingController(streamingService);
        DownloadController downloadController = new DownloadController(downloadService);
        RecommendationController recommendationController = new RecommendationController(recommendationService);

        // 2. Seed Music Catalog
        System.out.println("\n--------------------------------------------------");
        System.out.println("📍 1. SEEDING ARTISTS, ALBUMS & SONGS");
        System.out.println("--------------------------------------------------");
        Artist taylor = new Artist(1, "ART-01", "Taylor Swift", "https://cdn.music.com/taylor.jpg");
        Artist ed = new Artist(2, "ART-02", "Ed Sheeran", "https://cdn.music.com/ed.jpg");
        artistRepo.save(taylor);
        artistRepo.save(ed);

        Album album1989 = new Album(1, "ALB-01", "1989", 1, LocalDate.of(2014, 10, 27), "https://cdn.music.com/1989.jpg");
        Album divide = new Album(2, "ALB-02", "Divide", 2, LocalDate.of(2017, 3, 3), "https://cdn.music.com/divide.jpg");
        albumRepo.save(album1989);
        albumRepo.save(divide);

        Song s1 = new Song(1, "SONG-01", "Blank Space", 1, 1, 231, "Pop", "https://cdn.music.com/s1.mp3", "art1.jpg", 5000000, AudioQuality.PREMIUM, AudioFormat.MP3);
        Song s2 = new Song(2, "SONG-02", "Shake It Off", 1, 1, 219, "Pop", "https://cdn.music.com/s2.mp3", "art2.jpg", 4800000, AudioQuality.PREMIUM, AudioFormat.MP3);
        Song s3 = new Song(3, "SONG-03", "Shape of You", 2, 2, 233, "Pop", "https://cdn.music.com/s3.mp3", "art3.jpg", 5100000, AudioQuality.PREMIUM, AudioFormat.MP3);
        Song s4 = new Song(4, "SONG-04", "Perfect", 2, 2, 263, "Acoustic", "https://cdn.music.com/s4.mp3", "art4.jpg", 5500000, AudioQuality.PREMIUM, AudioFormat.MP3);
        songRepo.save(s1);
        songRepo.save(s2);
        songRepo.save(s3);
        songRepo.save(s4);

        System.out.println("🎵 Added: " + s1);
        System.out.println("🎵 Added: " + s2);
        System.out.println("🎵 Added: " + s3);
        System.out.println("🎵 Added: " + s4);

        // 3. User Onboarding
        System.out.println("\n--------------------------------------------------");
        System.out.println("📍 2. USER ONBOARDING (FREE VS PREMIUM)");
        System.out.println("--------------------------------------------------");
        User alice = new User(101, "alice", "alice@music.com", "Alice Smith", SubscriptionTier.PREMIUM);
        User bob = new User(102, "bob", "bob@music.com", "Bob Jones", SubscriptionTier.FREE);
        userRepo.save(alice);
        userRepo.save(bob);
        System.out.println("👤 " + alice);
        System.out.println("👤 " + bob);

        // 4. Search Flow
        System.out.println("\n--------------------------------------------------");
        System.out.println("📍 3. UNIFIED SEARCH");
        System.out.println("--------------------------------------------------");
        Map<String, Object> searchResults = searchController.search("Shape", "ALL");
        System.out.println("🔍 Search Results for 'Shape': " + searchResults);

        // 5. Unified Playback Session
        System.out.println("\n--------------------------------------------------");
        System.out.println("📍 4. PLAYBACK SESSIONS & QUEUE INITIALIZATION");
        System.out.println("--------------------------------------------------");
        // Alice plays Single Song
        PlaybackSession aliceSession = playbackController.play(alice.getId(), "SONG-01", PlaybackSource.SONG, "device_iphone_15");
        // Bob plays Album "Divide"
        PlaybackSession bobSession = playbackController.play(bob.getId(), "ALB-02", PlaybackSource.ALBUM, "device_android_tv");

        // 6. Chunk-based Audio Streaming & LRU Cache
        System.out.println("\n--------------------------------------------------");
        System.out.println("📍 5. CHUNK-BASED AUDIO STREAMING & LRU CACHE");
        System.out.println("--------------------------------------------------");
        // Request 1MB chunk (0 to 1048575 bytes) -> Cache Miss
        byte[] chunk1 = streamingController.stream("SONG-01", 0, 1048575, alice.getId());
        // Request same chunk again -> Cache Hit!
        byte[] chunk1Cached = streamingController.stream("SONG-01", 0, 1048575, alice.getId());

        // 7. Playback Controls & Progress Updates
        System.out.println("\n--------------------------------------------------");
        System.out.println("📍 6. PLAYBACK CONTROLS & LISTENING HISTORY");
        System.out.println("--------------------------------------------------");
        playbackController.pause(aliceSession.getSessionId());
        playbackController.resume(aliceSession.getSessionId());
        // Update playback position to 210s (over 90% of duration) -> Marks completed in history
        playbackController.updatePosition(aliceSession.getSessionId(), 210);

        // Bob skips next track in Album
        playbackController.skipNext(bobSession.getSessionId());

        // 8. Playlist Management
        System.out.println("\n--------------------------------------------------");
        System.out.println("📍 7. PLAYLIST CREATION & CONCURRENT UPDATES");
        System.out.println("--------------------------------------------------");
        Playlist favorites = playlistController.createPlaylist(alice.getId(), "My Roadtrip Hits", Arrays.asList("SONG-01", "SONG-03"));
        playlistController.addSongs(favorites.getPlaylistId(), alice.getId(), Collections.singletonList("SONG-04"));

        // 9. Offline Downloads Validation
        System.out.println("\n--------------------------------------------------");
        System.out.println("📍 8. OFFLINE DOWNLOADS (PREMIUM ONLY)");
        System.out.println("--------------------------------------------------");
        // Alice (Premium) downloads song
        Download aliceDl = downloadController.download(alice.getId(), "SONG-01", "device_iphone_15");

        // Bob (Free) attempts to download
        try {
            System.out.println("Bob (FREE user) attempting download...");
            downloadController.download(bob.getId(), "SONG-03", "device_android");
        } catch (IllegalStateException e) {
            System.out.println("❌ Rejected as expected: " + e.getMessage());
        }

        // 10. Recommendations via Strategy Pattern
        System.out.println("\n--------------------------------------------------");
        System.out.println("📍 9. PERSONALIZED RECOMMENDATIONS");
        System.out.println("--------------------------------------------------");
        List<Song> recommendations = recommendationController.getRecommendations(alice.getId());
        System.out.println("✨ Recommended Songs for Alice: " + recommendations);

        System.out.println("\n==================================================");
        System.out.println("✅ MUSIC STREAMING SIMULATION COMPLETED");
        System.out.println("==================================================");
    }
}
