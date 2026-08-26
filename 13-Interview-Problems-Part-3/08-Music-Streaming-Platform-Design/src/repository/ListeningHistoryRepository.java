package repository;

import domain.ListeningHistory;

import java.util.List;

public interface ListeningHistoryRepository {
    List<ListeningHistory> findByUserId(int userId);
    ListeningHistory save(ListeningHistory history);
}
