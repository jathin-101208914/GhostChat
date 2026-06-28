package com.ghostchat.websocket;

import com.ghostchat.dto.PresenceMessage;
import com.ghostchat.dto.SystemMessage;
import com.ghostchat.service.RedisService;
import com.ghostchat.service.RoomService;
import com.ghostchat.service.WebSocketSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class WebSocketDisconnectListener {
    private final WebSocketSessionService sessionService;
    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final RedisService redisService;

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event){
        String sessionId = event.getSessionId();

        String username = sessionService.getUsername(sessionId);
        String roomCode = sessionService.getRoomCode(sessionId);

//        System.out.println(
//                "DISCONNECT EVENT -> " +
//                        sessionId +
//                        " USER=" +
//                        username +
//                        " ROOM=" +
//                        roomCode
//        );

        if(username == null || roomCode == null){
            return;
        }

        roomService.leaveRoom(roomCode, username);

        Set<String> users;

        if(roomService.isAnonymousRoom(roomCode)){
            users = roomService.getAnonymousUsers(roomCode);
        }else{
            users = roomService.getRoomUsers(roomCode);
        }

        PresenceMessage presence = new PresenceMessage(roomCode, users);

        messagingTemplate.convertAndSend("/topic/presence/" + roomCode, presence);

        SystemMessage message = new SystemMessage();

        message.setRoomCode(roomCode);
        if (roomService.isAnonymousRoom(roomCode)){
            username = roomService.getAnonymousName(roomCode, username);
            message.setContent("🔴 " + username + " disconnected");
            message.setType("SYSTEM");
            message.setUsername(username);
        }
        else {
            message.setContent("🔴 " + username + " disconnected");
            message.setType("SYSTEM");
            message.setUsername(username);
        }


        messagingTemplate.convertAndSend("/topic/room/" + roomCode, message);
        sessionService.removeSession(sessionId);

//        System.out.println("DISCNNECTED -> " + username);
    }
}
