package com.ghostchat.controller;

import com.ghostchat.dto.CreateRoomRequest;
import com.ghostchat.dto.JoinRoomRequest;
import com.ghostchat.dto.LeaveRoomRequest;
import com.ghostchat.dto.SystemMessage;
import com.ghostchat.service.InviteService;
import com.ghostchat.service.MessageService;
import com.ghostchat.service.RedisService;
import com.ghostchat.service.RoomService;
import com.ghostchat.websocket.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final RedisService redisService;
    private final InviteService inviteService;

    @PostMapping("/create")
    public String createRoom(@RequestBody CreateRoomRequest request){

        return roomService.createRoom(request.isAnonymousMode());
    }

    @PostMapping("/join")
    public String joinRoom(@RequestBody JoinRoomRequest request){

        return roomService.joinRoom(request.getRoomCode(), request.getUsername());

    }

    @PostMapping("/leave")
    public String leaveRoom(@RequestBody LeaveRoomRequest request){
//        System.out.println("LEAVE API HIT -> " + request.getUsername());
        return roomService.leaveRoom(request.getRoomCode(), request.getUsername());
    }

    @GetMapping("/all")
    public ConcurrentHashMap<String, Set<String>> getAllRooms(){
        return roomService.getRooms();
    }

    @GetMapping("/{roomCode}/users")
    public Set<String> getRoomUsers(@PathVariable String roomCode){
        return redisService.getRoomUsersAsString(roomCode);
    }

    @GetMapping("/{roomCode}/alias/{username}")
    public  String getAlias(@PathVariable String roomCode, @PathVariable String username){
        return roomService.getAnonymousName(roomCode, username);
    }

    @GetMapping("/{roomCode}/anonymous-users")
    public Set<String> getAnonymousUsers(@PathVariable String roomCode){
        return roomService.getAnonymousUsers(roomCode);
    }

    @GetMapping("/{roomCode}/anonymous")
    public boolean isAnonymousRoom(@PathVariable String roomCode){
        return roomService.isAnonymousRoom((roomCode));
    }

    @GetMapping("/{roomCode}/messages")
    public List<ChatMessage> getMessages(@PathVariable String roomCode){
        return messageService.getMessages(roomCode);
    }

    @GetMapping("/{roomCode}/invite")
    public String createInvite(@PathVariable String roomCode){
        return inviteService.createToken();
    }

    @GetMapping("/validate/{token}")
    public boolean validateInvite(
            @PathVariable String token
    ){
        return inviteService.isValidToken(token);
    }

    @PostMapping("/consume/{token}")
    public void consumeInvite(@PathVariable String token){
        inviteService.consumeToken(token);
    }
}
