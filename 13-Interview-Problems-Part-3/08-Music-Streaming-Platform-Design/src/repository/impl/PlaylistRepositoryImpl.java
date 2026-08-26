package repository.impl;

import domain.Playlist;
import repository.PlaylistRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PlaylistRepositoryImpl implements PlaylistRepository {
    private final Map<String, Playlist> playlists = new ConcurrentHashMap<>();

    @Override
    public Optional<Playlist> findById(int id) {
        return playlists.values().stream().filter(p -> p.getId() == id).findFirst();
    }

    @Override
    public Optional<Playlist> findByPlaylistId(String playlistId) {
        return Optional.ofNullable(playlists.get(playlistId));
    }

    @Override
    public List<Playlist> findByUserId(int userId) {
        return playlists.values().stream().filter(p -> p.getUserId() == userId).collect(Collectors.toList());
    }

    @Override
    public List<Playlist> findByNameContaining(String name) {
        return playlists.values().stream()
                .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Playlist save(Playlist playlist) {
        playlists.put(playlist.getPlaylistId(), playlist);
        return playlist;
    }

    @Override
    public void deleteByPlaylistId(String playlistId) {
        playlists.remove(playlistId);
    }
}
