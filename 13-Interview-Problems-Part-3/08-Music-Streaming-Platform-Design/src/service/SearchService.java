package service;

import domain.Album;
import domain.Artist;
import domain.Playlist;
import domain.Song;
import repository.AlbumRepository;
import repository.ArtistRepository;
import repository.PlaylistRepository;
import repository.SongRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchService {
    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final PlaylistRepository playlistRepository;

    public SearchService(SongRepository songRepository, ArtistRepository artistRepository,
                         AlbumRepository albumRepository, PlaylistRepository playlistRepository) {
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.playlistRepository = playlistRepository;
    }

    public Map<String, Object> search(String query, String type) {
        Map<String, Object> results = new HashMap<>();

        if (type == null || type.equalsIgnoreCase("ALL") || type.equalsIgnoreCase("SONG")) {
            List<Song> songs = songRepository.findByTitleContaining(query);
            results.put("songs", songs);
        }
        if (type == null || type.equalsIgnoreCase("ALL") || type.equalsIgnoreCase("ARTIST")) {
            List<Artist> artists = artistRepository.findByNameContaining(query);
            results.put("artists", artists);
        }
        if (type == null || type.equalsIgnoreCase("ALL") || type.equalsIgnoreCase("ALBUM")) {
            List<Album> albums = albumRepository.findByTitleContaining(query);
            results.put("albums", albums);
        }
        if (type == null || type.equalsIgnoreCase("ALL") || type.equalsIgnoreCase("PLAYLIST")) {
            List<Playlist> playlists = playlistRepository.findByNameContaining(query);
            results.put("playlists", playlists);
        }

        return results;
    }
}
