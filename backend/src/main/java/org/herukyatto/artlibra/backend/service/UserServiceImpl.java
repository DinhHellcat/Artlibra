package org.herukyatto.artlibra.backend.service;

import lombok.RequiredArgsConstructor;
import org.herukyatto.artlibra.backend.dto.AdminCreateUserRequest;
import org.herukyatto.artlibra.backend.dto.UserProfileResponse;
import org.herukyatto.artlibra.backend.entity.Role;
import org.herukyatto.artlibra.backend.entity.User;
import org.herukyatto.artlibra.backend.repository.RoleRepository;
import org.herukyatto.artlibra.backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.herukyatto.artlibra.backend.dto.UpdateProfileRequest;
import org.springframework.web.multipart.MultipartFile;
import org.herukyatto.artlibra.backend.dto.AdminUserViewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.herukyatto.artlibra.backend.exception.ResourceNotFoundException;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService { // <<== Thêm UserService vào đây

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

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
    @Override
    @Transactional
    public UserProfileResponse updateUserProfile(UpdateProfileRequest request) {
        // Lấy user đang đăng nhập từ context bảo mật
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof User)) {
            throw new IllegalStateException("User not authenticated correctly.");
        }
        User currentUser = (User) principal;

        // Cập nhật các trường thông tin
        currentUser.setFullName(request.getFullName());
        currentUser.setPhone(request.getPhone());
        // TODO: Thêm logic cập nhật avatarUrl sau này

        // Lưu lại user đã được cập nhật vào CSDL
        User updatedUser = userRepository.save(currentUser);

        // Chuyển đổi entity đã cập nhật thành DTO để trả về
        return UserProfileResponse.builder()
                .id(updatedUser.getId())
                .email(updatedUser.getEmail())
                .fullName(updatedUser.getFullName())
                .avatarUrl(updatedUser.getAvatarUrl())
                .phone(updatedUser.getPhone())
                .roles(updatedUser.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet()))
                .build();
    }

    @Override
    @Transactional
    public UserProfileResponse updateAvatar(MultipartFile file) {
        // Lấy user đang đăng nhập
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof User)) {
            throw new IllegalStateException("User not authenticated correctly.");
        }
        User currentUser = (User) principal;

        // Tải file lên Cloudinary
        String avatarUrl = cloudinaryService.uploadFile(file);

        // Cập nhật đường dẫn avatar mới cho user
        currentUser.setAvatarUrl(avatarUrl);
        User updatedUser = userRepository.save(currentUser);

        // Trả về profile đã cập nhật
        return UserProfileResponse.builder()
                // ... các trường khác ...
                .id(updatedUser.getId())
                .email(updatedUser.getEmail())
                .fullName(updatedUser.getFullName())
                .avatarUrl(updatedUser.getAvatarUrl())
                .phone(updatedUser.getPhone())
                .roles(updatedUser.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet()))
                .build();
    }

    @Override
    public Page<AdminUserViewResponse> getAllUsers(Pageable pageable) {
        // Lấy một trang người dùng từ CSDL
        Page<User> userPage = userRepository.findAll(pageable);
        // Chuyển đổi mỗi User Entity sang AdminUserViewResponse DTO
        return userPage.map(this::convertToAdminViewDto);
    }

    // Hàm tiện ích để chuyển đổi
    private AdminUserViewResponse convertToAdminViewDto(User user) {
        return AdminUserViewResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .emailVerified(user.isEmailVerified())
                .createdAt(user.getCreatedAt())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet()))
                .build();
    }

    @Override
    public void banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setActive(false); // Đặt trạng thái không hoạt động
        userRepository.save(user);
    }

    // === PHƯƠNG THỨC MỚI ĐỂ MỞ KHÓA USER ===
    @Override
    public void unbanUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setActive(true); // Đặt trạng thái hoạt động trở lại
        userRepository.save(user);
    }

    @Override
    public User createUserByAdmin(AdminCreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use.");
        }

        // Tìm vai trò được chỉ định trong CSDL
        Role userRole = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .roles(Set.of(userRole))
                .emailVerified(request.isEmailVerified()) // Lấy trạng thái xác thực từ request
                .active(true)
                .build();

        return userRepository.save(user);
    }
}