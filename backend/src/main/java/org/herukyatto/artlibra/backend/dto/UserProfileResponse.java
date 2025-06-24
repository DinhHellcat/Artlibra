package org.herukyatto.artlibra.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String phone; // <<== THÊM TRƯỜG CÒN THIẾU VÀO ĐÂY
    private Set<String> roles;
}