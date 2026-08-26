package domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Album {
    private final int id;
    private final String albumId;
    private final String title;
    private final int artistId;
    private final LocalDate releaseDate;
    private final String thumbnailUrl;
    private final LocalDateTime createdAt;

    public Album(int id, String albumId, String title, int artistId, LocalDate releaseDate, String thumbnailUrl) {
        this.id = id;
        this.albumId = albumId;
        this.title = title;
        this.artistId = artistId;
        this.releaseDate = releaseDate;
        this.thumbnailUrl = thumbnailUrl;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public String getAlbumId() {
        return albumId;
    }

    public String getTitle() {
        return title;
    }

    public int getArtistId() {
        return artistId;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Album[" + title + " (ID=" + albumId + ")]";
    }
}
