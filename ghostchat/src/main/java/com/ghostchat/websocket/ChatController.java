package com.ghostchat.websocket;

import com.ghostchat.dto.PresenceMessage;
import com.ghostchat.dto.SystemMessage;
import com.ghostchat.dto.TypingMessage;
import com.ghostchat.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Set;

@Controller
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final RoomService roomService;
    private final WebSocketSessionService webSocketSessionService;
    private final MessageService messageService;
    private final RedisService redisService;
    private final MessageExpiryService messageExpiryService;

    public ChatController(SimpMessagingTemplate messagingTemplate, RoomService roomService, WebSocketSessionService webSocketSessionService, MessageService messageService, RedisService redisService, MessageExpiryService messageExpiryService){
        this.messagingTemplate = messagingTemplate;
        this.roomService = roomService;
        this.webSocketSessionService = webSocketSessionService;
        this.messageService = messageService;
        this.redisService = redisService;
        this.messageExpiryService = messageExpiryService;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessage message){
        if(roomService.isAnonymousRoom(message.getRoomCode())){
            String alias = roomService.getAnonymousName(message.getRoomCode(), message.getSender());
            message.setSender(alias);
        }
        messageService.saveMessage(message);
        messageExpiryService.expireMessage(message);

        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomCode(), message);
//        System.out.println(
//                "ROOM " +
//                        message.getRoomCode() +
//                        " Anonymous = " +
//                        roomService.isAnonymousRoom(message.getRoomCode())
//        );
    }

    @MessageMapping("/chat.typing")
    public void typing(@Payload TypingMessage message){
        // if you want alias use this
        if(message.isAnonymousMode()) {
            String alias = roomService.getAnonymousName(message.getRoomCode(), message.getUsername());
            message.setUsername(alias);
        }
        messagingTemplate.convertAndSend("/topic/typing/" + message.getRoomCode(), message);
    }

    @MessageMapping("/chat.join")
    public void joinRoom(@Payload SystemMessage message, SimpMessageHeaderAccessor headerAccessor){
        String sessionId = headerAccessor.getSessionId();

        webSocketSessionService.addSession(sessionId, message.getUsername(), message.getRoomCode());

        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomCode(), message);

        Set<String> users;

        if(roomService.isAnonymousRoom(message.getRoomCode())){
            users = roomService.getAnonymousUsers(message.getRoomCode());
        } else {
            users = roomService.getRoomUsers(message.getRoomCode());
        }
        PresenceMessage presence =
                new PresenceMessage(
                        message.getRoomCode(),
                        users
                );

        messagingTemplate.convertAndSend(
                "/topic/presence/" + message.getRoomCode(),
                presence
        );
    }

    @MessageMapping("/chat.leave")
    public void leaveRoom(@Payload SystemMessage message){
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomCode(), message);
    }
}
