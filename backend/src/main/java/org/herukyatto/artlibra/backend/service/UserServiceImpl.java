package org.herukyatto.artlibra.backend.service;

import lombok.RequiredArgsConstructor;
import org.herukyatto.artlibra.backend.dto.UserProfileResponse;
import org.herukyatto.artlibra.backend.entity.User;
import org.herukyatto.artlibra.backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService { // <<== Thêm UserService vào đây

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    @Override
    public UserProfileResponse getCurrentUserProfile() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof User) {
            User currentUser = (User) principal;
            return UserProfileResponse.builder()
                    .id(currentUser.getId())
                    .email(currentUser.getEmail())
                    .fullName(currentUser.getFullName())
                    .avatarUrl(currentUser.getAvatarUrl())
                    .roles(currentUser.getRoles().stream()
                            .map(role -> role.getName().name())
                            .collect(Collectors.toSet()))
                    .build();
        }
        throw new IllegalStateException("Could not find the authenticated user.");
    }

    // === PHƯƠNG THỨC MỚI ĐỂ XÓA USER ===
    @Override
    @Transactional // Đảm bảo các thao tác xóa được thực hiện trong một giao dịch
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        // Do có quan hệ ManyToMany, cần xóa user khỏi các role trước, hoặc để cascade
        // Cách đơn giản nhất là xóa trực tiếp, JPA sẽ xử lý bảng trung gian user_roles
        userRepository.deleteById(userId);
        System.out.println("Successfully deleted user with ID: " + userId);
    }
}