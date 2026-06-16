package com.ghostchat.controller;

import com.ghostchat.dto.LoginRequest;
import com.ghostchat.dto.LoginResponse;
import com.ghostchat.dto.RegisterRequest;
import com.ghostchat.service.AuthService;
import com.ghostchat.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request){
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        return authService.login(request);
    }

    @GetMapping("/token")
    public String generationToken(){
        return jwtUtil.generateToken("test@gmail.com");
    }
}
