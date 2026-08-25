package repository;

import domain.Comment;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CommentRepository {
    private final Map<Integer, Comment> comments = new ConcurrentHashMap<>();

    public Comment save(Comment comment) {
        comments.put(comment.getId(), comment);
        return comment;
    }

    public List<Comment> findByTaskId(int taskId) {
        return comments.values().stream()
                .filter(c -> c.getTaskId() == taskId)
                .collect(Collectors.toList());
    }
}
