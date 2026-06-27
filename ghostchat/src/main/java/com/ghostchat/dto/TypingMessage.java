package com.ghostchat.dto;

import lombok.Data;

@Data
public class TypingMessage {
    private String roomCode;
    private String username;
    private String realUsername;
    private boolean typing;
    private boolean anonymousMode;
}
