package service;

import domain.Playlist;
import repository.PlaylistRepository;
import repository.SongRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;
    private final LockService lockService;
    private final AtomicInteger idCounter = new AtomicInteger(1);

    public PlaylistService(PlaylistRepository playlistRepository, SongRepository songRepository, LockService lockService) {
        this.playlistRepository = playlistRepository;
        this.songRepository = songRepository;
        this.lockService = lockService;
    }

    public Playlist createPlaylist(int userId, String name, List<String> songIds) {
        int id = idCounter.getAndIncrement();
        String playlistId = "PL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Playlist playlist = new Playlist(id, playlistId, name, userId, false, songIds);
        playlistRepository.save(playlist);
        System.out.println("🎵 " + playlist + " created.");
        return playlist;
    }

    public Playlist updatePlaylist(String playlistId, int userId, String name, List<String> songIds) {
        String lockKey = "playlist_lock_" + playlistId;
        if (!lockService.acquire(lockKey, 500)) {
            throw new IllegalStateException("Could not acquire lock for playlist " + playlistId);
        }
        try {
            Playlist playlist = playlistRepository.findByPlaylistId(playlistId)
                    .orElseThrow(() -> new IllegalArgumentException("Playlist " + playlistId + " not found."));

            if (playlist.getUserId() != userId) {
                throw new IllegalAccessException("User " + userId + " does not own playlist " + playlistId);
            }

            if (name != null) playlist.setName(name);
            if (songIds != null) {
                for (String songId : songIds) {
                    playlist.addSong(songId);
                }
            }
            playlistRepository.save(playlist);
            System.out.println("📝 " + playlist + " updated.");
            return playlist;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            lockService.release(lockKey);
        }
    }

    public void deletePlaylist(String playlistId, int userId) {
        Playlist playlist = playlistRepository.findByPlaylistId(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist " + playlistId + " not found."));

        if (playlist.getUserId() == userId) {
            playlistRepository.deleteByPlaylistId(playlistId);
            System.out.println("🗑️ Playlist " + playlistId + " deleted.");
        }
    }

    public Playlist addSongs(String playlistId, int userId, List<String> songIds) {
        return updatePlaylist(playlistId, userId, null, songIds);
    }
}
