package rs.raf.demo.controllers;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendStatusUpdate(String message) {
        this.messagingTemplate.convertAndSend("/topic/status", message);
    }
}
