package controller;

import domain.PlaybackSession;
import domain.PlaybackSource;
import domain.RepeatMode;
import service.PlaybackService;

public class PlaybackController {
    private final PlaybackService playbackService;

    public PlaybackController(PlaybackService playbackService) {
        this.playbackService = playbackService;
    }

    public PlaybackSession play(int userId, String sourceId, PlaybackSource sourceType, String deviceId) {
        return playbackService.play(userId, sourceId, sourceType, deviceId);
    }

    public PlaybackSession pause(String sessionId) {
        return playbackService.pause(sessionId);
    }

    public PlaybackSession resume(String sessionId) {
        return playbackService.resume(sessionId);
    }

    public PlaybackSession skipNext(String sessionId) {
        return playbackService.skipNext(sessionId);
    }

    public PlaybackSession skipPrevious(String sessionId) {
        return playbackService.skipPrevious(sessionId);
    }

    public PlaybackSession toggleShuffle(String sessionId, boolean enabled) {
        return playbackService.toggleShuffle(sessionId, enabled);
    }

    public PlaybackSession setRepeatMode(String sessionId, RepeatMode mode) {
        return playbackService.setRepeatMode(sessionId, mode);
    }

    public void updatePosition(String sessionId, long position) {
        playbackService.updatePosition(sessionId, position);
    }

    public PlaybackSession getState(String sessionId) {
        return playbackService.getState(sessionId);
    }
}
