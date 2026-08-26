package repository.impl;

import domain.Song;
import repository.SongRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SongRepositoryImpl implements SongRepository {
    private final Map<Integer, Song> songs = new ConcurrentHashMap<>();

    @Override
    public Optional<Song> findById(int id) {
        return Optional.ofNullable(songs.get(id));
    }

    @Override
    public Optional<Song> findBySongId(String songId) {
        return songs.values().stream().filter(s -> s.getSongId().equalsIgnoreCase(songId)).findFirst();
    }

    @Override
    public List<Song> findByTitleContaining(String title) {
        return songs.values().stream()
                .filter(s -> s.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Song> findByArtistId(int artistId) {
        return songs.values().stream().filter(s -> s.getArtistId() == artistId).collect(Collectors.toList());
    }

    @Override
    public List<Song> findByAlbumId(int albumId) {
        return songs.values().stream().filter(s -> s.getAlbumId() != null && s.getAlbumId() == albumId).collect(Collectors.toList());
    }

    @Override
    public List<Song> findByGenre(String genre) {
        return songs.values().stream()
                .filter(s -> s.getGenre().equalsIgnoreCase(genre))
                .collect(Collectors.toList());
    }

    @Override
    public List<Song> findAll() {
        return new ArrayList<>(songs.values());
    }

    @Override
    public Song save(Song song) {
        songs.put(song.getId(), song);
        return song;
    }
}
