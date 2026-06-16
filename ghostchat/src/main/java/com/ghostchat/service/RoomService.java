package com.ghostchat.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomService {
    private final ConcurrentHashMap<String, Set<String>> rooms = new ConcurrentHashMap<>();

    public String createRoom(){
        String roomCode = UUID.randomUUID()
                .toString()
                .substring(0, 6)
                .toUpperCase();
        rooms.put(roomCode, ConcurrentHashMap.newKeySet());

        return roomCode;
    }

    public String joinRoom(String roomCode, String username){
        if (!rooms.containsKey(roomCode)) {
            return "Room not found";
        }
        rooms.get(roomCode).add(username);

        return username + " joined room " + roomCode;
    }

    public String leaveRoom(String roomCode, String username){
        if(!rooms.containsKey(roomCode)){
            return "Room not found";
        }

        Set<String> users = rooms.get(roomCode);
        users.remove(username);

        if(users.isEmpty()){
            rooms.remove(roomCode);

            return "Room Destroyed";
        }
        return username + "left room" + roomCode;
    }

    public ConcurrentHashMap<String, Set<String>> getRooms() {
        return rooms;
    }
}