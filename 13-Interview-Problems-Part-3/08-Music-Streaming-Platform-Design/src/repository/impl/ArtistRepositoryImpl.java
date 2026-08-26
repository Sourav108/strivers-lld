package repository.impl;

import domain.Artist;
import repository.ArtistRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ArtistRepositoryImpl implements ArtistRepository {
    private final Map<Integer, Artist> artists = new ConcurrentHashMap<>();

    @Override
    public Optional<Artist> findById(int id) {
        return Optional.ofNullable(artists.get(id));
    }

    @Override
    public Optional<Artist> findByArtistId(String artistId) {
        return artists.values().stream().filter(a -> a.getArtistId().equalsIgnoreCase(artistId)).findFirst();
    }

    @Override
    public List<Artist> findByNameContaining(String name) {
        return artists.values().stream()
                .filter(a -> a.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Artist save(Artist artist) {
        artists.put(artist.getId(), artist);
        return artist;
    }
}
