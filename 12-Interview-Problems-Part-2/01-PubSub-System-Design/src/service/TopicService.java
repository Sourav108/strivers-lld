package service;

import domain.Topic;
import repository.TopicRepository;

import java.util.List;
import java.util.UUID;

public class TopicService {
    private final TopicRepository topicRepository;

    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public Topic createTopic(String name) {
        String id = "TOPIC-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Topic topic = new Topic(id, name);
        topicRepository.save(topic);
        System.out.println("📢 [Topic Created] " + topic);
        return topic;
    }

    public Topic getTopic(String topicId) {
        return topicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Topic #" + topicId + " not found."));
    }

    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    public void deactivateTopic(String topicId) {
        Topic topic = getTopic(topicId);
        topic.setActive(false);
        topicRepository.save(topic);
        System.out.println("⛔ [Topic Deactivated] " + topic);
    }
}
