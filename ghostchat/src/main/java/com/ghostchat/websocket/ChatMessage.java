package com.ghostchat.websocket;

import lombok.Data;

@Data
public class ChatMessage {
    private String roomCode;
    private String sender;
    private String content;
}
