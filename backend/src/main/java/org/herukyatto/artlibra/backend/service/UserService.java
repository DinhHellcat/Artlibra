package org.herukyatto.artlibra.backend.service;

import org.herukyatto.artlibra.backend.dto.UserProfileResponse;
import org.herukyatto.artlibra.backend.dto.UpdateProfileRequest; // <<== THÊM DÒNG NÀY
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserProfileResponse getCurrentUserProfile();
    void deleteUser(Long userId);
    UserProfileResponse updateUserProfile(UpdateProfileRequest request);
    UserProfileResponse updateAvatar(MultipartFile file); // <<== THÊM DÒNG NÀY
}