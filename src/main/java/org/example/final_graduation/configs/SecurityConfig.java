package org.example.final_graduation.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.cors.CorsConfiguration;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Bean để mã hóa mật khẩu
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Cấu hình Spring Security
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // TEMPORARY: Disable CSRF if causing issues
                .cors(cors -> cors.configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues()))  // Enable CORS
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/promotions/validate").hasAuthority("ROLE_USER")
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")  // Chỉ Admin có thể truy cập /admin/**
//                        .requestMatchers("/user/**").hasAuthority("ROLE_USER")  // Chỉ Customer có thể truy cập /user/**
                        .requestMatchers("/", "/login", "/signup", "/css/**", "/js/**","/product/**").permitAll()  // Trang mở cho tất cả
                        .anyRequest().authenticated()  // Các trang còn lại yêu cầu đăng nhập
                )
                .formLogin(form -> form
                        .loginPage("/login")  // Trang đăng nhập tùy chỉnh
                        .loginProcessingUrl("/perform_login")  // URL xử lý đăng nhập
                        .successHandler(customAuthenticationSuccessHandler())  // Xử lý sau khi đăng nhập thành công
                        .failureUrl("/login?error=true")  // Chuyển hướng nếu đăng nhập thất bại
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")  // URL để logout
                        .logoutSuccessUrl("/login?logout=true")  // Chuyển hướng sau khi logout
                        .invalidateHttpSession(true)  // Xóa session
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    // Xử lý chuyển hướng sau khi đăng nhập
    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {
                if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                    response.sendRedirect("/admin/orders"); // Admin về trang /admin
                } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"))) {
                    response.sendRedirect("/staff"); // Staff về trang /staff
                } else {
                    response.sendRedirect("/"); // Customer về trang chủ
                }
            }
        };
    }
}
