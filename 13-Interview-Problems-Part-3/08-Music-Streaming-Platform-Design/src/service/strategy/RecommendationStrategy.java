package service.strategy;

import domain.ListeningHistory;
import domain.Song;
import repository.SongRepository;

import java.util.List;

public interface RecommendationStrategy {
    List<Song> generate(int userId, List<ListeningHistory> history, SongRepository songRepository);
}
