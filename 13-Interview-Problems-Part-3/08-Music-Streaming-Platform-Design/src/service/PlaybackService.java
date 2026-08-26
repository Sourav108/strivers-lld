package service;

import domain.*;
import repository.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class PlaybackService {
    private final PlaybackSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final PlaylistRepository playlistRepository;
    private final ListeningHistoryRepository historyRepository;
    private final StreamingService streamingService;

    private final AtomicInteger sessionIdCounter = new AtomicInteger(1);
    private final AtomicInteger historyIdCounter = new AtomicInteger(1);

    public PlaybackService(PlaybackSessionRepository sessionRepository, UserRepository userRepository,
                           SongRepository songRepository, AlbumRepository albumRepository,
                           PlaylistRepository playlistRepository, ListeningHistoryRepository historyRepository,
                           StreamingService streamingService) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.songRepository = songRepository;
        this.albumRepository = albumRepository;
        this.playlistRepository = playlistRepository;
        this.historyRepository = historyRepository;
        this.streamingService = streamingService;
    }

    public PlaybackSession play(int userId, String sourceId, PlaybackSource sourceType, String deviceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User " + userId + " not found."));

        List<String> queue = new ArrayList<>();
        String initialSongId = null;

        if (sourceType == PlaybackSource.SONG) {
            queue.add(sourceId);
            initialSongId = sourceId;
        } else if (sourceType == PlaybackSource.ALBUM) {
            Album album = albumRepository.findByAlbumId(sourceId)
                    .orElseThrow(() -> new IllegalArgumentException("Album " + sourceId + " not found."));
            List<Song> albumSongs = songRepository.findByAlbumId(album.getId());
            queue = albumSongs.stream().map(Song::getSongId).collect(Collectors.toList());
            if (!queue.isEmpty()) initialSongId = queue.get(0);
        } else if (sourceType == PlaybackSource.PLAYLIST) {
            Playlist playlist = playlistRepository.findByPlaylistId(sourceId)
                    .orElseThrow(() -> new IllegalArgumentException("Playlist " + sourceId + " not found."));
            queue = new ArrayList<>(playlist.getSongIds());
            if (!queue.isEmpty()) initialSongId = queue.get(0);
        }

        if (queue.isEmpty() || initialSongId == null) {
            throw new IllegalStateException("Cannot initiate playback with an empty track queue.");
        }

        int id = sessionIdCounter.getAndIncrement();
        String sessionId = "SESS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        PlaybackSession session = new PlaybackSession(id, sessionId, userId, initialSongId, sourceType, sourceId, queue, deviceId);
        sessionRepository.save(session);

        AudioQuality quality = user.isPremium() ? AudioQuality.PREMIUM : AudioQuality.STANDARD;
        String streamUrl = streamingService.getStreamUrl(initialSongId, quality);

        System.out.println("▶️ Playback initiated: " + session + " [Stream: " + streamUrl + "]");
        return session;
    }

    public PlaybackSession pause(String sessionId) {
        PlaybackSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session " + sessionId + " not found."));
        session.setStatus(PlaybackStatus.PAUSED);
        sessionRepository.save(session);
        System.out.println("⏸️ Playback PAUSED: " + session);
        return session;
    }

    public PlaybackSession resume(String sessionId) {
        PlaybackSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session " + sessionId + " not found."));
        session.setStatus(PlaybackStatus.PLAYING);
        sessionRepository.save(session);
        System.out.println("▶️ Playback RESUMED: " + session);
        return session;
    }

    public PlaybackSession skipNext(String sessionId) {
        PlaybackSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session " + sessionId + " not found."));

        User user = userRepository.findById(session.getUserId()).orElse(null);
        // Free user skip limitation could be checked here

        String previousSongId = session.getCurrentSongId();
        saveListeningHistory(session.getUserId(), previousSongId, session.getCurrentPosition(), false);

        String nextSong = session.skipNext();
        sessionRepository.save(session);
        System.out.println("⏭️ Skipped NEXT: " + session);
        return session;
    }

    public PlaybackSession skipPrevious(String sessionId) {
        PlaybackSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session " + sessionId + " not found."));
        session.skipPrevious();
        sessionRepository.save(session);
        System.out.println("⏮️ Skipped PREVIOUS: " + session);
        return session;
    }

    public PlaybackSession toggleShuffle(String sessionId, boolean enabled) {
        PlaybackSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session " + sessionId + " not found."));
        session.setShuffleMode(enabled);
        sessionRepository.save(session);
        System.out.println("🔀 Shuffle mode set to " + enabled + " for session " + sessionId);
        return session;
    }

    public PlaybackSession setRepeatMode(String sessionId, RepeatMode mode) {
        PlaybackSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session " + sessionId + " not found."));
        session.setRepeatMode(mode);
        sessionRepository.save(session);
        System.out.println("🔁 Repeat mode set to " + mode + " for session " + sessionId);
        return session;
    }

    public void updatePosition(String sessionId, long position) {
        PlaybackSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session " + sessionId + " not found."));
        session.updatePosition(position);

        songRepository.findBySongId(session.getCurrentSongId()).ifPresent(song -> {
            boolean completed = position >= (song.getDuration() * 0.9);
            if (completed) {
                saveListeningHistory(session.getUserId(), song.getSongId(), position, true);
            }
        });
        sessionRepository.save(session);
    }

    public void saveListeningHistory(int userId, String songId, long playDuration, boolean completed) {
        int id = historyIdCounter.getAndIncrement();
        ListeningHistory history = new ListeningHistory(id, userId, songId, playDuration, completed);
        historyRepository.save(history);
    }

    public PlaybackSession getState(String sessionId) {
        return sessionRepository.findBySessionId(sessionId).orElse(null);
    }
}
