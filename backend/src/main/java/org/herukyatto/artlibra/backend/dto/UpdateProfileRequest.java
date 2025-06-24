package org.herukyatto.artlibra.backend.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private String phone;
    // Chúng ta sẽ thêm avatarUrl ở các bước sau khi làm tính năng upload ảnh
}