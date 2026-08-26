package repository;

import domain.Artist;

import java.util.List;
import java.util.Optional;

public interface ArtistRepository {
    Optional<Artist> findById(int id);
    Optional<Artist> findByArtistId(String artistId);
    List<Artist> findByNameContaining(String name);
    Artist save(Artist artist);
}
