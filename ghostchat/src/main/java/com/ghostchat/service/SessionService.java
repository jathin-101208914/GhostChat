package com.ghostchat.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {

    private final ConcurrentHashMap<String, String> activeSession = new ConcurrentHashMap<>();

    public void saveSession(String email, String sessionId){
        activeSession.put(email, sessionId);
    }

    public String getSessionToken(String email){
        return activeSession.get(email);
    }

    public void removeSession(String email){
        activeSession.remove(email);
    }
}
