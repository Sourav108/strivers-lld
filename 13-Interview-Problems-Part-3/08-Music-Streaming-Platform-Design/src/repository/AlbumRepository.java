package repository;

import domain.Album;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository {
    Optional<Album> findById(int id);
    Optional<Album> findByAlbumId(String albumId);
    List<Album> findByArtistId(int artistId);
    List<Album> findByTitleContaining(String title);
    Album save(Album album);
}
