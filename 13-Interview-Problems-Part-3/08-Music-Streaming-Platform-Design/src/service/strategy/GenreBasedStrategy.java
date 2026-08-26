package service.strategy;

import domain.ListeningHistory;
import domain.Song;
import repository.SongRepository;

import java.util.*;
import java.util.stream.Collectors;

public class GenreBasedStrategy implements RecommendationStrategy {

    @Override
    public List<Song> generate(int userId, List<ListeningHistory> history, SongRepository songRepository) {
        if (history == null || history.isEmpty()) {
            return songRepository.findAll().stream().limit(5).collect(Collectors.toList());
        }

        // Count preferred genres from history
        Map<String, Long> genreCounts = new HashMap<>();
        for (ListeningHistory lh : history) {
            songRepository.findBySongId(lh.getSongId()).ifPresent(song -> {
                genreCounts.put(song.getGenre(), genreCounts.getOrDefault(song.getGenre(), 0L) + 1);
            });
        }

        String topGenre = genreCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Pop");

        Set<String> alreadyPlayed = history.stream().map(ListeningHistory::getSongId).collect(Collectors.toSet());

        return songRepository.findByGenre(topGenre).stream()
                .filter(s -> !alreadyPlayed.contains(s.getSongId()))
                .limit(5)
                .collect(Collectors.toList());
    }
}
