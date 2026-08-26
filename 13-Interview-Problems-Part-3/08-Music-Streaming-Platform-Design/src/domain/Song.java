package domain;

import java.time.LocalDateTime;

public class Song {
    private final int id;
    private final String songId;
    private final String title;
    private final int artistId;
    private final Integer albumId;
    private final long duration; // in seconds
    private final String genre;
    private final String audioUrl;
    private final String thumbnailUrl;
    private final long fileSize; // in bytes
    private final AudioQuality quality;
    private final AudioFormat format;
    private final LocalDateTime createdAt;

    public Song(int id, String songId, String title, int artistId, Integer albumId,
                long duration, String genre, String audioUrl, String thumbnailUrl,
                long fileSize, AudioQuality quality, AudioFormat format) {
        this.id = id;
        this.songId = songId;
        this.title = title;
        this.artistId = artistId;
        this.albumId = albumId;
        this.duration = duration;
        this.genre = genre;
        this.audioUrl = audioUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.fileSize = fileSize;
        this.quality = quality;
        this.format = format;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public String getSongId() {
        return songId;
    }

    public String getTitle() {
        return title;
    }

    public int getArtistId() {
        return artistId;
    }

    public Integer getAlbumId() {
        return albumId;
    }

    public long getDuration() {
        return duration;
    }

    public String getGenre() {
        return genre;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public long getFileSize() {
        return fileSize;
    }

    public AudioQuality getQuality() {
        return quality;
    }

    public AudioFormat getFormat() {
        return format;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getFormattedDuration() {
        long mins = duration / 60;
        long secs = duration % 60;
        return String.format("%d:%02d", mins, secs);
    }

    @Override
    public String toString() {
        return "Song[" + title + " (" + getFormattedDuration() + ") | Genre=" + genre + " | ID=" + songId + "]";
    }
}
