package repository.impl;

import domain.Album;
import repository.AlbumRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AlbumRepositoryImpl implements AlbumRepository {
    private final Map<Integer, Album> albums = new ConcurrentHashMap<>();

    @Override
    public Optional<Album> findById(int id) {
        return Optional.ofNullable(albums.get(id));
    }

    @Override
    public Optional<Album> findByAlbumId(String albumId) {
        return albums.values().stream().filter(a -> a.getAlbumId().equalsIgnoreCase(albumId)).findFirst();
    }

    @Override
    public List<Album> findByArtistId(int artistId) {
        return albums.values().stream().filter(a -> a.getArtistId() == artistId).collect(Collectors.toList());
    }

    @Override
    public List<Album> findByTitleContaining(String title) {
        return albums.values().stream()
                .filter(a -> a.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Album save(Album album) {
        albums.put(album.getId(), album);
        return album;
    }
}
