package controller;

import domain.Message;
import service.PublisherService;

public class PublisherController {
    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    public Message publishMessage(String topicId, String content) {
        return publisherService.publishMessage(topicId, content);
    }
}
