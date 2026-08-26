package controller;

import domain.Song;
import service.RecommendationService;

import java.util.List;

public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    public List<Song> getRecommendations(int userId) {
        return recommendationService.getRecommendations(userId);
    }
}
