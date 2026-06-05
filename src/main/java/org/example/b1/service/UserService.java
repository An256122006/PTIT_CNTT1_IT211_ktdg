package org.example.b1.service;

import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import org.example.b1.common.RoleEnum;
import org.example.b1.model.dto.AuthResponseDto;
import org.example.b1.model.dto.LoginRequestDto;
import org.example.b1.model.dto.UserRequestDto;
import org.example.b1.model.entity.ReflectToken;
import org.example.b1.model.entity.User;
import org.example.b1.repository.ReflectRepository;
import org.example.b1.repository.UserRepository;
import org.example.b1.security.jwt.JwtService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final ReflectRepository reflectRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public User register(UserRequestDto request) {
        User user = new User();
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        user.setUsername(request.getUsername());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole(RoleEnum.ROLE_USER);
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        user.setEmail(request.getEmail());
        return userRepository.save(user);
    }
    public AuthResponseDto login(LoginRequestDto request) {
        Optional<User> user = userRepository.findByUsername(request.getUsername());
        if(user.isEmpty()) {
            throw new RuntimeException("Username not found");
        }
        String accessToken = jwtService.generateToken(user.get(),24*60*60*1000L);
        String refreshToken = jwtService.generateToken(user.get(),7*24*60*60*1000L);
        reflectRepository.save(new ReflectToken(null,refreshToken,user.get(),false,new Date(new Date().getTime() +7*24*60*60*1000)));
        return new AuthResponseDto(accessToken,refreshToken);
    }
    public AuthResponseDto refreshToken(String refreshToken) {
        boolean check=jwtService.validateToken(refreshToken);
        if (!check) {
            throw new RuntimeException("Invalid token");
        }
        String username=jwtService.extractUsername(refreshToken);
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isEmpty()) {
            throw new RuntimeException("Username not found");
        }
        String token = jwtService.generateToken(user.get(),24*60*60*1000L);
        return  new AuthResponseDto(refreshToken,token);
    }
}
