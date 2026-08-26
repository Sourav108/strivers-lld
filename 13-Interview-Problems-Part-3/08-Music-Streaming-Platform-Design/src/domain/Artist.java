package domain;

import java.time.LocalDateTime;

public class Artist {
    private final int id;
    private final String artistId;
    private final String name;
    private final String thumbnailUrl;
    private final LocalDateTime createdAt;

    public Artist(int id, String artistId, String name, String thumbnailUrl) {
        this.id = id;
        this.artistId = artistId;
        this.name = name;
        this.thumbnailUrl = thumbnailUrl;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public String getArtistId() {
        return artistId;
    }

    public String getName() {
        return name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Artist[" + name + " (ID=" + artistId + ")]";
    }
}
