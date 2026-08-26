package service;

import domain.ListeningHistory;
import domain.Song;
import repository.ListeningHistoryRepository;
import repository.SongRepository;
import service.strategy.GenreBasedStrategy;
import service.strategy.RecommendationStrategy;

import java.util.List;

public class RecommendationService {
    private final ListeningHistoryRepository historyRepository;
    private final SongRepository songRepository;
    private RecommendationStrategy recommendationStrategy;

    public RecommendationService(ListeningHistoryRepository historyRepository, SongRepository songRepository) {
        this.historyRepository = historyRepository;
        this.songRepository = songRepository;
        this.recommendationStrategy = new GenreBasedStrategy();
    }

    public void setRecommendationStrategy(RecommendationStrategy strategy) {
        this.recommendationStrategy = strategy;
    }

    public List<Song> getRecommendations(int userId) {
        List<ListeningHistory> history = historyRepository.findByUserId(userId);
        return recommendationStrategy.generate(userId, history, songRepository);
    }
}
