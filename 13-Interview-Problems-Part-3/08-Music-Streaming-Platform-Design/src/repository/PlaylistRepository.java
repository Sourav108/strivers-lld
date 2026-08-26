package repository;

import domain.Playlist;

import java.util.List;
import java.util.Optional;

public interface PlaylistRepository {
    Optional<Playlist> findById(int id);
    Optional<Playlist> findByPlaylistId(String playlistId);
    List<Playlist> findByUserId(int userId);
    List<Playlist> findByNameContaining(String name);
    Playlist save(Playlist playlist);
    void deleteByPlaylistId(String playlistId);
}
