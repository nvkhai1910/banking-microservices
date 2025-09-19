package com.banking.auth.service.service;

import com.banking.auth.service.dto.request.LoginRequest;
import com.banking.auth.service.dto.request.RegisterRequest;
import com.banking.auth.service.dto.response.AuthResponse;
import com.banking.auth.service.entity.Role;
import com.banking.auth.service.entity.User;
import com.banking.auth.service.mapper.UserMapper;
import com.banking.auth.service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    JwtService jwtService;
    UserMapper userMapper;

    public AuthResponse register(RegisterRequest registerRequest) {
        if(userRepository.findByEmail(registerRequest.getEmail()).isPresent()){
            throw new RuntimeException("Email already exists");
        }

        User user = userMapper.toEntity(registerRequest);

        System.out.println("Request: " + registerRequest);
        System.out.println("User after mapping: " + user);

        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);

        String token = jwtService.generateToken(user.getUsername());

        return AuthResponse.builder().token(token).build();
    }

    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new RuntimeException("Invalid password");
        }
        String token = jwtService.generateToken(user.getEmail());
        return AuthResponse.builder().token(token).build();
    }
}
