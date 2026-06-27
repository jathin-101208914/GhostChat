package com.ghostchat.service;

import com.ghostchat.websocket.ChatMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessageService {

    private final ConcurrentHashMap<String, List<ChatMessage>> roomMessages = new ConcurrentHashMap<>();

    public void saveMessage(ChatMessage message){
        roomMessages.computeIfAbsent(message.getRoomCode(), k -> new ArrayList<>()).add(message);
    }

    public List<ChatMessage> getMessages(String roomCode){
        return roomMessages.getOrDefault(roomCode, new ArrayList<>());
    }

    public void deleteRoomMessages(String roomCode){
        roomMessages.remove(roomCode);
    }

    public void deleteMessage(ChatMessage message){
        List<ChatMessage> messages = roomMessages.get(message.getRoomCode());

        if(messages != null){
            messages.remove(message);
        }
    }
}
