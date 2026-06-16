package com.ghostchat.controller;

import com.ghostchat.dto.JoinRoomRequest;
import com.ghostchat.dto.LeaveRoomRequest;
import com.ghostchat.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/create")
    public String createRoom(){
        return roomService.createRoom();
    }

    @PostMapping("/join")
    public String joinRoom(@RequestBody JoinRoomRequest request){
        return roomService.joinRoom(request.getRoomCode(), request.getUsername());
    }

    @PostMapping("/leave")
    public String leaveRoom(@RequestBody LeaveRoomRequest request){
        return roomService.leaveRoom(request.getRoomCode(), request.getUsername());
    }

    @GetMapping("/all")
    public ConcurrentHashMap<String, Set<String>> getAllRooms(){
        return roomService.getRooms();
    }
}
