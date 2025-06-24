package org.herukyatto.artlibra.backend.config;

import lombok.RequiredArgsConstructor;
import org.herukyatto.artlibra.backend.entity.Role;
import org.herukyatto.artlibra.backend.entity.RoleName;
import org.herukyatto.artlibra.backend.repository.RoleRepository;
import org.herukyatto.artlibra.backend.security.JwtAuthenticationFilter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod; // <<== IMPORT MỚI

import java.util.Arrays;

/**
 * Lớp cấu hình chính cho Spring Security.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // Inject các bean cần thiết qua constructor
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    /**
     * Định nghĩa chuỗi bộ lọc bảo mật (Security Filter Chain).
     * Đây là nơi cấu hình các quy tắc truy cập cho các đường dẫn URL.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Vô hiệu hóa CSRF (Cross-Site Request Forgery) vì chúng ta dùng JWT, không dùng session cookie.
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Cấu hình quy tắc phân quyền cho các request.
                .authorizeHttpRequests(authorize -> authorize
                        // Cho phép tất cả mọi người truy cập vào các đường dẫn bắt đầu bằng "/api/auth/"
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").permitAll()
                        // Tất cả các request còn lại đều yêu cầu phải xác thực.
                        .anyRequest().authenticated()
                )

                // 3. Quản lý phiên làm việc (session). Đặt là STATELESS để không tạo session phía server.
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. Đăng ký AuthenticationProvider mà chúng ta đã định nghĩa ở dưới.
                .authenticationProvider(authenticationProvider())

                // 5. Thêm JWT filter của chúng ta vào trước bộ lọc mặc định của Spring.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Bean để mã hóa mật khẩu.
     * Sử dụng thuật toán BCrypt mạnh mẽ.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean cung cấp cơ chế xác thực.
     * Nó kết nối UserDetailsService (để lấy user) và PasswordEncoder (để so sánh mật khẩu).
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Bean quản lý chính cho quá trình xác thực.
     * Controller sẽ gọi đến nó.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Bean này sẽ được chạy một lần duy nhất khi ứng dụng khởi động.
     * Dùng để khởi tạo các dữ liệu cần thiết như vai trò (Roles).
     */
    @Bean
    CommandLineRunner initDatabase(RoleRepository roleRepository) {
        return args -> {
            Arrays.stream(RoleName.values()).forEach(roleName -> {
                if (roleRepository.findByName(roleName).isEmpty()) {
                    Role newRole = new Role();
                    newRole.setName(roleName);
                    roleRepository.save(newRole);
                    System.out.println("Created role: " + roleName);
                }
            });
        };
    }
}