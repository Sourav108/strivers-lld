package repository.impl;

import domain.ListeningHistory;
import repository.ListeningHistoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ListeningHistoryRepositoryImpl implements ListeningHistoryRepository {
    private final Map<Integer, ListeningHistory> historyMap = new ConcurrentHashMap<>();

    @Override
    public List<ListeningHistory> findByUserId(int userId) {
        return historyMap.values().stream().filter(h -> h.getUserId() == userId).collect(Collectors.toList());
    }

    @Override
    public ListeningHistory save(ListeningHistory history) {
        historyMap.put(history.getId(), history);
        return history;
    }
}
