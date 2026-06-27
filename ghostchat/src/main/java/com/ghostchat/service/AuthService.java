package com.ghostchat.service;

import com.ghostchat.dto.LoginRequest;
import com.ghostchat.dto.LoginResponse;
import com.ghostchat.dto.RegisterRequest;
import com.ghostchat.entity.User;
import com.ghostchat.repository.UserRepository;
import com.ghostchat.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final SessionService sessionService;
    private final SimpMessagingTemplate messagingTemplate;

    public String register(RegisterRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            return "Email already exists";
        }

        if(userRepository.existsByUsername(request.getUsername())){
            return "Username already exists";
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        return "User registered successfully";
    }

    public LoginResponse login(LoginRequest request){
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElse(null);

        if(user == null){
            return new LoginResponse(null, null, null);
        }

        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if(!matches){
            return new LoginResponse(null, null, null);
        }

        messagingTemplate.convertAndSend("/topic/logout" + user.getEmail(), "LOGOUT");

        String sessionId = UUID.randomUUID().toString();

        String token = jwtUtil.generateToken(user.getEmail(), sessionId);

        sessionService.saveSession(user.getEmail(), sessionId);

        return new LoginResponse(token, user.getUsername(), user.getEmail());
    }
}
