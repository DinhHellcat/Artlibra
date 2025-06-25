package org.herukyatto.artlibra.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonManagedReference; // <<== IMPORT MỚI

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true, exclude = {"commissionsAsClient", "commissionsAsArtist"}) // Thêm exclude để tránh vòng lặp vô hạn
@ToString(exclude = {"commissionsAsClient", "commissionsAsArtist"}) // Thêm exclude
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends AbstractEntity implements UserDetails {

    // ... các trường cũ không đổi ...
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(name = "full_name", nullable = false)
    private String fullName;
    @Column(name = "avatar_url")
    private String avatarUrl;
    @Column(nullable = false)
    private String phone;
    @Builder.Default
    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;
    @Column(name = "email_verification_token")
    private String emailVerificationToken;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    // ==========================================================
    // === THÊM MỐI QUAN HỆ ONE-TO-MANY VỚI COMMISSION ===
    // ==========================================================

    // Danh sách các commission mà user này là Client
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("user-client-commissions") // <<== THÊM DÒNG NÀY
    private Set<Commission> commissionsAsClient = new HashSet<>();

    // Danh sách các commission mà user này là Artist
    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("user-artist-commissions") // <<== THÊM DÒNG NÀY
    private Set<Commission> commissionsAsArtist = new HashSet<>();

    // ===================================================================
    // CÁC PHƯƠNG THỨC ĐƯỢC IMPLEMENT TỪ UserDetails
    // ===================================================================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Chuyển đổi Set<Role> thành List<SimpleGrantedAuthority>
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        // Spring Security sẽ dùng email làm username
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Tài khoản không bao giờ hết hạn
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Tài khoản không bị khóa
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Mật khẩu không bao giờ hết hạn
    }

    @Override
    public boolean isEnabled() {
        return true; // Tài khoản được kích hoạt
    }
}