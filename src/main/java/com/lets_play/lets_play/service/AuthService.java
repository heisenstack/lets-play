package com.lets_play.lets_play.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lets_play.lets_play.dto.AuthResponse;
import com.lets_play.lets_play.dto.RegisterRequest;
import com.lets_play.lets_play.model.User;
import com.lets_play.lets_play.model.Role;
import com.lets_play.lets_play.repository.UserRepository;
import com.lets_play.lets_play.security.JwtService;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
     public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

     public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        User user = new User(
                request.getUsername(),
                request.getEmail().toLowerCase().trim(),    
                passwordEncoder.encode(request.getPassword()),
                Role.USER
        );

        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved.getId(), saved.getRole().name());

        return new AuthResponse(token, saved.getId(), saved.getUsername(),
                                saved.getEmail(), saved.getRole().name());
    }

}
