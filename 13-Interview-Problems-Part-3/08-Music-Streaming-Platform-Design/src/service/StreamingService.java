package service;

import domain.AudioQuality;
import domain.Song;
import repository.SongRepository;

import java.util.Arrays;
import java.util.Optional;

public class StreamingService {
    private final SongRepository songRepository;
    private final CacheService cacheService;

    public StreamingService(SongRepository songRepository, CacheService cacheService) {
        this.songRepository = songRepository;
        this.cacheService = cacheService;
    }

    public String getStreamUrl(String songId, AudioQuality quality) {
        Song song = songRepository.findBySongId(songId)
                .orElseThrow(() -> new IllegalArgumentException("Song " + songId + " not found."));
        return song.getAudioUrl() + "?quality=" + quality.name().toLowerCase();
    }

    public byte[] getChunk(String songId, long start, long end) {
        Optional<byte[]> cachedChunk = cacheService.getChunk(songId, start, end);
        if (cachedChunk.isPresent()) {
            return cachedChunk.get();
        }

        // Simulate fetching chunk from storage/CDN
        int chunkSize = (int) (end - start + 1);
        byte[] chunk = new byte[Math.max(1, chunkSize)];
        Arrays.fill(chunk, (byte) 0xAA); // Simulated audio payload

        cacheService.putChunk(songId, start, end, chunk);
        return chunk;
    }

    public void downloadFullSong(String songId, String deviceId) {
        Song song = songRepository.findBySongId(songId)
                .orElseThrow(() -> new IllegalArgumentException("Song " + songId + " not found."));
        System.out.println("📥 [StreamingService] Downloading full audio file for " + song.getTitle() +
                " (" + song.getFileSize() / 1024 + " KB) to device: " + deviceId);
    }
}
