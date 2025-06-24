package org.herukyatto.artlibra.backend.service;

import org.herukyatto.artlibra.backend.dto.UserProfileResponse;

public interface UserService {
    UserProfileResponse getCurrentUserProfile();
    void deleteUser(Long userId);
}