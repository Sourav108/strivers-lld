package controller;

import domain.Playlist;
import service.PlaylistService;

import java.util.List;

public class PlaylistController {
    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    public Playlist createPlaylist(int userId, String name, List<String> songIds) {
        return playlistService.createPlaylist(userId, name, songIds);
    }

    public Playlist updatePlaylist(String playlistId, int userId, String name, List<String> songIds) {
        return playlistService.updatePlaylist(playlistId, userId, name, songIds);
    }

    public void deletePlaylist(String playlistId, int userId) {
        playlistService.deletePlaylist(playlistId, userId);
    }

    public Playlist addSongs(String playlistId, int userId, List<String> songIds) {
        return playlistService.addSongs(playlistId, userId, songIds);
    }
}
