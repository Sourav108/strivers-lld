package domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Playlist {
    private final int id;
    private final String playlistId;
    private String name;
    private final int userId;
    private boolean isPublic;
    private final List<String> songIds;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Playlist(int id, String playlistId, String name, int userId, boolean isPublic, List<String> songIds) {
        this.id = id;
        this.playlistId = playlistId;
        this.name = name;
        this.userId = userId;
        this.isPublic = isPublic;
        this.songIds = new ArrayList<>(songIds != null ? songIds : Collections.emptyList());
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }

    public int getUserId() {
        return userId;
    }

    public synchronized boolean isPublic() {
        return isPublic;
    }

    public synchronized void setPublic(boolean aPublic) {
        isPublic = aPublic;
        this.updatedAt = LocalDateTime.now();
    }

    public synchronized List<String> getSongIds() {
        return Collections.unmodifiableList(new ArrayList<>(songIds));
    }

    public synchronized void addSong(String songId) {
        if (!songIds.contains(songId)) {
            songIds.add(songId);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public synchronized void removeSong(String songId) {
        songIds.remove(songId);
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public synchronized LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public synchronized String toString() {
        return "Playlist[" + name + " (ID=" + playlistId + ", Songs=" + songIds.size() + ", Public=" + isPublic + ")]";
    }
}
