package org.herukyatto.artlibra.backend.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        // In ra lỗi xác thực thật sự vào console để chúng ta có thể gỡ lỗi
        System.err.println("Authentication error from Entry Point: " + authException.getMessage());

        // Trả về lỗi 401 Unauthorized, đây là mã lỗi đúng cho việc xác thực thất bại.
        // Đính kèm thông điệp lỗi để client có thể biết chuyện gì đã xảy ra.
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized - " + authException.getMessage());
    }
}