package org.herukyatto.artlibra.backend.service;

import org.herukyatto.artlibra.backend.dto.UserProfileResponse;
import org.herukyatto.artlibra.backend.dto.UpdateProfileRequest;
import org.springframework.web.multipart.MultipartFile;
import org.herukyatto.artlibra.backend.dto.AdminUserViewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserProfileResponse getCurrentUserProfile();
    void deleteUser(Long userId);
    UserProfileResponse updateUserProfile(UpdateProfileRequest request);
    UserProfileResponse updateAvatar(MultipartFile file);
    Page<AdminUserViewResponse> getAllUsers(Pageable pageable);
    void banUser(Long userId);
    void unbanUser(Long userId);
}