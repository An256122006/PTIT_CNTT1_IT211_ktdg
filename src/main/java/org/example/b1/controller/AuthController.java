package org.example.b1.controller;

import lombok.RequiredArgsConstructor;
import org.example.b1.model.dto.AuthResponseDto;
import org.example.b1.model.dto.LoginRequestDto;
import org.example.b1.model.dto.RefreshTokenRequestDto;
import org.example.b1.model.dto.UserRequestDto;
import org.example.b1.model.entity.User;
import org.example.b1.security.jwt.JwtService;
import org.example.b1.security.principal.UserPrincipal;
import org.example.b1.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserRequestDto userRequestDto) {
       return ResponseEntity.status(HttpStatus.OK).body(userService.register(userRequestDto));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.login(loginRequestDto));
    }
    @PostMapping("/refreshToken")
    public ResponseEntity<AuthResponseDto> refreshToken(@RequestBody RefreshTokenRequestDto request) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.refreshToken(request.getRefreshToken()));
    }

    @GetMapping("/user/me")
    public Object getCurrentUser(@AuthenticationPrincipal UserPrincipal userDetails) {
        return userDetails.getUser();
    }
}
