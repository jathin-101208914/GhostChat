package com.ghostchat.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
public class RoomService {
    @Getter
    private final ConcurrentHashMap<String, Set<String>> rooms = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, String>> anonymousUsers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Boolean> anonymousRooms = new ConcurrentHashMap<String, Boolean>();
    private final MessageService messageService;
    private final RedisService redisService;

    public String createRoom(boolean anonymousMode){
        String roomCode = UUID.randomUUID()
                .toString()
                .substring(0, 6)
                .toUpperCase();
        rooms.put(roomCode, ConcurrentHashMap.newKeySet());

        anonymousRooms.put(roomCode, anonymousMode);
        System.out.println("ROOM CREATED -> " + roomCode + " Anonymous: " + anonymousMode);

//        System.out.println("ROOM CREATED: " + roomCode);
//        System.out.println("ROOMS: " + rooms);
        return roomCode;
    }

    public String joinRoom(String roomCode, String username){
        if (!rooms.containsKey(roomCode)) {
            return "Room not found";
        }
        redisService.addUserToRoom(roomCode, username);

//        System.out.println("USER JOINED: " + username);
//        System.out.println("ROOMS: " + rooms);
        System.out.println(
                "REDIS JOIN -> " + username + " ROOM=" + roomCode
        );
        return username + " joined room " + roomCode;
    }

    public String leaveRoom(String roomCode, String username){
        if(!rooms.containsKey(roomCode)){
            return "Room not found";
        }

        Set<String> users = rooms.get(roomCode);
        redisService.removeUserFromRoom(roomCode, username);
//        System.out.println("USER LEFT: " + username);
//        System.out.println("ROOMS: " + rooms);

        if(redisService.roomSize(roomCode) == 0){
            rooms.remove(roomCode);
            redisService.deleteRoom(roomCode);
            anonymousRooms.remove(roomCode);

            System.out.println("DELETING ROOM -> " + roomCode);
            messageService.deleteRoomMessages(roomCode);
//            System.out.println("ROOM DESTROYED: " + roomCode);
//            System.out.println("ROOMS: " + rooms);
            System.out.println("MESSAGES DELETED -> " + roomCode);

            return "Room Destroyed";
        }
        return username + "left room" + roomCode;
    }

    public Set<String> getRoomUsers(String roomCode){
        return redisService.getRoomUsers(roomCode);
    }

    public String getAnonymousName(String roomCode, String username){
        anonymousUsers.putIfAbsent(roomCode, new ConcurrentHashMap<>());
        ConcurrentHashMap<String, String> roomAliases = anonymousUsers.get(roomCode);

        return roomAliases.computeIfAbsent(username, u -> "Ghost-" + (1000+(int)(Math.random()*9000)));
    }

    public Set<String> getAnonymousUsers(String roomCode){
        Set<String> aliases = new HashSet<>();

        Set<String> users = redisService.getRoomUsers(roomCode);

        for(String user : users){
            aliases.add(getAnonymousName(roomCode, user));
        }

        return aliases;
    }

    public boolean isAnonymousRoom(String roomCode){
        return anonymousRooms.getOrDefault(roomCode, false);
    }
}