package org.herukyatto.artlibra.backend.controller;

import lombok.RequiredArgsConstructor;
import org.herukyatto.artlibra.backend.dto.UserProfileResponse;
import org.herukyatto.artlibra.backend.service.UserService; // <<== Đổi thành interface
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService; // <<== Đổi thành interface

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    // === ENDPOINT MỚI ĐỂ XÓA USER ===
    // TODO: Sau này sẽ thêm @PreAuthorize("hasRole('ADMIN')") để chỉ admin được xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("User with ID " + id + " has been deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting user: " + e.getMessage());
        }
    }
}