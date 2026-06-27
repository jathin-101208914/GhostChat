package com.ghostchat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void save(String key, Object value){
        redisTemplate.opsForValue().set(key, value);
    }

    public Object get(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key){
        redisTemplate.delete(key);
    }

    public void addUserToRoom(String roomCode, String username){
        redisTemplate.opsForSet().add("room:" + roomCode, username);
    }

    @SuppressWarnings("unchecked")
    public Set<String> getRoomUsers(String roomCode){
        return (Set<String>) (Set<?>) redisTemplate.opsForSet().members("room:" + roomCode);
    }

    public void removeUserFromRoom(String roomCode, String username){
        redisTemplate.opsForSet().remove("room:" + roomCode, username);
    }

    public long roomSize(String roomCode){
        return redisTemplate.opsForSet().size("room:" + roomCode);
    }

    public void deleteRoom(String roomCode){
        redisTemplate.delete("room:" + roomCode);
    }

    public Set<String> getRoomUsersAsString(String roomCode){
        Set<Object> users = redisTemplate.opsForSet().members("room:" + roomCode);

        if(users == null){
            return Set.of();
        }

        return users.stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
    }
}
