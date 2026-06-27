package com.ghostchat.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketSessionService {

    private final ConcurrentHashMap<String, String> sessionUsers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> sessionRooms = new ConcurrentHashMap<>();

    public void addSession(String sessionId, String username, String roomCode){
        sessionUsers.put(sessionId, username);
        sessionRooms.put(sessionId, roomCode);

//        System.out.println(
//                "SESSION ADDED -> " +
//                        sessionId +
//                        " USER=" +
//                        username +
//                        " ROOM=" +
//                        roomCode
//        );
    }

    public String getUsername(String sessionId){
        return sessionUsers.get(sessionId);
    }

    public String getRoomCode(String sessionId){
        return sessionRooms.get(sessionId);
    }

    public void removeSession(String sessionId){
        sessionUsers.remove(sessionId);
        sessionRooms.remove(sessionId);
    }
}
