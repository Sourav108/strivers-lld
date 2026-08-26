package service.strategy;

import domain.ListeningHistory;
import domain.Song;
import repository.SongRepository;

import java.util.List;
import java.util.stream.Collectors;

public class CollaborativeFilteringStrategy implements RecommendationStrategy {

    @Override
    public List<Song> generate(int userId, List<ListeningHistory> history, SongRepository songRepository) {
        return songRepository.findAll().stream().skip(1).limit(5).collect(Collectors.toList());
    }
}
