package org.herukyatto.artlibra.backend.dto;

import lombok.Data;
import org.herukyatto.artlibra.backend.entity.RoleName; // Import RoleName enum

@Data
public class SignUpRequest {
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private RoleName role;
}