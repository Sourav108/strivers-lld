package controller;

import service.MessageService;

public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    public void acknowledgeMessage(String messageId, String subscriberId) {
        messageService.acknowledgeMessage(messageId, subscriberId);
    }
}
