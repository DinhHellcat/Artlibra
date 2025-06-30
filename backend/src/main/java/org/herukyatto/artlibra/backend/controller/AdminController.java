package org.herukyatto.artlibra.backend.controller;

import lombok.RequiredArgsConstructor;
import org.herukyatto.artlibra.backend.dto.AdminUserViewResponse;
import org.herukyatto.artlibra.backend.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.herukyatto.artlibra.backend.dto.AdminCreateUserRequest;
import org.herukyatto.artlibra.backend.entity.User;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AdminUserViewResponse>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @PostMapping("/users/{id}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> banUser(@PathVariable Long id) {
        userService.banUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/{id}/unban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> unbanUser(@PathVariable Long id) {
        userService.unbanUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminUserViewResponse> createUserByAdmin(@Valid @RequestBody AdminCreateUserRequest request) {
        User createdUser = userService.createUserByAdmin(request);

        // Chuyển đổi User vừa tạo sang DTO để trả về
        AdminUserViewResponse responseDto = AdminUserViewResponse.builder()
                .id(createdUser.getId())
                .email(createdUser.getEmail())
                .fullName(createdUser.getFullName())
                .phone(createdUser.getPhone())
                .emailVerified(createdUser.isEmailVerified())
                .createdAt(createdUser.getCreatedAt())
                .roles(createdUser.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet()))
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}