package com.ghostchat.service;

import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InviteService {

    private final ConcurrentHashMap<String, Boolean> inviteTokens = new ConcurrentHashMap<>();

    public String createToken() {
        String token = UUID.randomUUID().toString().substring(0, 8);

        inviteTokens.put(token, false);

        return token;
    }

    public boolean isValidToken(String token){
        return inviteTokens.containsKey(token) && !inviteTokens.get(token);
    }

    public void consumeToken(String token){
        inviteTokens.put(token, true);
    }
}
