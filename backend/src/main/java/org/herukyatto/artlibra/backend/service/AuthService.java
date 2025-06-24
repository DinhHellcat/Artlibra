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
import org.springframework.mail.SimpleMailMessage; // Import mới
import org.springframework.mail.javamail.JavaMailSender; // Import mới
import java.util.UUID; // Import mới

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider; // <<== THÊM VÀO
    private final JavaMailSender mailSender; // <<== Inject Mail Sender

    public User signUp(SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use.");
        }
        if (signUpRequest.getRole() == null || (signUpRequest.getRole() != RoleName.ROLE_CLIENT && signUpRequest.getRole() != RoleName.ROLE_ARTIST)) {
            throw new IllegalArgumentException("A valid role (ROLE_CLIENT or ROLE_ARTIST) must be selected.");
        }

        Role userRole = roleRepository.findByName(signUpRequest.getRole())
                .orElseThrow(() -> new RuntimeException("Error: Selected role is not found."));

        String verificationToken = UUID.randomUUID().toString();

        User user = User.builder()
                .fullName(signUpRequest.getFullName())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .phone(signUpRequest.getPhone())
                .roles(Set.of(userRole))
                .emailVerified(false)
                .emailVerificationToken(verificationToken)
                .build();

        User savedUser = userRepository.save(user);

        // --- BỌC PHẦN GỬI EMAIL TRONG TRY-CATCH ---
        try {
            sendVerificationEmail(savedUser);
            System.out.println("Verification email sent successfully to " + savedUser.getEmail());
        } catch (Exception e) {
            // In ra lỗi email để chúng ta gỡ lỗi, nhưng không làm sập luồng đăng ký
            System.err.println("Error sending verification email to " + savedUser.getEmail() + ": " + e.getMessage());
        }
        // ------------------------------------------

        return savedUser;
    }

    // Phương thức mới để gửi email
    private void sendVerificationEmail(User user) {
        String verificationLink = "http://localhost:8080/api/auth/verify-email?token=" + user.getEmailVerificationToken();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("ArtLibra - Xác thực tài khoản của bạn");
        message.setText("Chào " + user.getFullName() + ",\n\nVui lòng nhấn vào đường dẫn dưới đây để xác thực tài khoản của bạn:\n" + verificationLink);
        mailSender.send(message);
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