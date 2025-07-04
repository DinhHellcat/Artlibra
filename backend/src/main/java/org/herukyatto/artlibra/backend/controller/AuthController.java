package org.herukyatto.artlibra.backend.controller;

import lombok.RequiredArgsConstructor;
import org.herukyatto.artlibra.backend.dto.LoginRequest;
import org.herukyatto.artlibra.backend.dto.SignUpRequest;
import org.herukyatto.artlibra.backend.entity.User;
import org.herukyatto.artlibra.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.herukyatto.artlibra.backend.dto.JwtAuthenticationResponse;
import org.springframework.web.bind.annotation.GetMapping; // Import mới
import org.springframework.web.bind.annotation.RequestParam; // Import mới

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        try {
            User registeredUser = authService.signUp(signUpRequest);
            return ResponseEntity.ok("User registered successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.signIn(loginRequest));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        try {
            authService.verifyEmail(token); // <<== GỌI LOGIC THẬT SỰ
            return ResponseEntity.ok("Xác thực email thành công! Bạn có thể đăng nhập ngay bây giờ.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}