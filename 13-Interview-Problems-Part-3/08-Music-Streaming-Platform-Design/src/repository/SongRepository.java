package repository;

import domain.Song;

import java.util.List;
import java.util.Optional;

public interface SongRepository {
    Optional<Song> findById(int id);
    Optional<Song> findBySongId(String songId);
    List<Song> findByTitleContaining(String title);
    List<Song> findByArtistId(int artistId);
    List<Song> findByAlbumId(int albumId);
    List<Song> findByGenre(String genre);
    List<Song> findAll();
    Song save(Song song);
}
