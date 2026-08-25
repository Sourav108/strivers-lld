package controller;

import domain.Topic;
import service.TopicService;

import java.util.List;

public class TopicController {
    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    public Topic createTopic(String name) {
        return topicService.createTopic(name);
    }

    public List<Topic> getAllTopics() {
        return topicService.getAllTopics();
    }

    public void deactivateTopic(String topicId) {
        topicService.deactivateTopic(topicId);
    }
}
