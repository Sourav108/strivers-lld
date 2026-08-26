package controller;

import service.StreamingService;

public class StreamingController {
    private final StreamingService streamingService;

    public StreamingController(StreamingService streamingService) {
        this.streamingService = streamingService;
    }

    public byte[] stream(String songId, long start, long end, int userId) {
        return streamingService.getChunk(songId, start, end);
    }
}
