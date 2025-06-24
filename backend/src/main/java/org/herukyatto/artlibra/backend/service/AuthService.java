package org.herukyatto.artlibra.backend.service;

import lombok.RequiredArgsConstructor;
import org.herukyatto.artlibra.backend.dto.JwtAuthenticationResponse;
import org.herukyatto.artlibra.backend.dto.LoginRequest;
import org.herukyatto.artlibra.backend.dto.SignUpRequest;
import org.herukyatto.artlibra.backend.entity.Role;
import org.herukyatto.artlibra.backend.entity.RoleName;
import org.herukyatto.artlibra.backend.entity.User;
import org.herukyatto.artlibra.backend.repository.RoleRepository;
import org.herukyatto.artlibra.backend.repository.UserRepository;
import org.herukyatto.artlibra.backend.security.JwtTokenProvider; // <<== IMPORT MỚI
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider; // <<== THÊM VÀO

    public User signUp(SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use.");
        }
        User user = new User();
        user.setFullName(signUpRequest.getFullName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        Role userRole = roleRepository.findByName(RoleName.ROLE_CLIENT)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        user.setRoles(Set.of(userRole));
        return userRepository.save(user);
    }

    public JwtAuthenticationResponse signIn(LoginRequest loginRequest) {
        // Xác thực người dùng
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        // Nếu xác thực thành công, tìm user và tạo token
        var user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        var jwt = jwtTokenProvider.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }
}