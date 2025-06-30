package org.herukyatto.artlibra.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserViewResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private boolean emailVerified;
    private Instant createdAt;
    private Set<String> roles;
}