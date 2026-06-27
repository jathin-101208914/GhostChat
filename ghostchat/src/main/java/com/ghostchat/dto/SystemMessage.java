package com.ghostchat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemMessage {

    private String roomCode;
    private String content;
    private String type;
    private String username;

}
