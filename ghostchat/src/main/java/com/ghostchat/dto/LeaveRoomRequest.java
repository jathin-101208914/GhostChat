package com.ghostchat.dto;

import lombok.Data;

@Data
public class LeaveRoomRequest {

    private String roomCode;
    private String username;
}
