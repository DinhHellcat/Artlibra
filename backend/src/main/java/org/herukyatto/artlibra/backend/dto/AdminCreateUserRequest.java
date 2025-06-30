package org.herukyatto.artlibra.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.herukyatto.artlibra.backend.entity.RoleName;

@Data
public class AdminCreateUserRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank
    private String fullName;

    @NotBlank
    private String phone;

    @NotNull(message = "Role cannot be null")
    private RoleName role; // Admin có thể chọn vai trò (CLIENT, ARTIST, hoặc ADMIN)

    private boolean emailVerified = true; // Mặc định tài khoản do Admin tạo đã được xác thực
}