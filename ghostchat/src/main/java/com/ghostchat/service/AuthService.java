package com.ghostchat.service;

import com.ghostchat.dto.LoginRequest;
import com.ghostchat.dto.LoginResponse;
import com.ghostchat.dto.RegisterRequest;
import com.ghostchat.entity.User;
import com.ghostchat.repository.UserRepository;
import com.ghostchat.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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
            return new LoginResponse(" User not found");
        }

        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if(!matches){
            return new LoginResponse("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new LoginResponse(token);
    }
}
