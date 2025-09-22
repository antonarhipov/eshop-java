package org.example.eshop.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.firewall.HttpFirewall
import org.springframework.security.web.firewall.StrictHttpFirewall

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun httpFirewall(): HttpFirewall {
        val firewall = StrictHttpFirewall()
        firewall.setAllowSemicolon(true)
        return firewall
    }

    @Bean
    fun userDetailsService(): UserDetailsService {
        val admin: UserDetails = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("admin123"))
            .roles("ADMIN")
            .build()

        return InMemoryUserDetailsManager(admin)
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { authz ->
                authz
                    // Admin endpoints require ADMIN role
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    // Public API endpoints
                    .requestMatchers("/api/products/**").permitAll()
                    .requestMatchers("/api/cart/**").permitAll()
                    .requestMatchers("/api/checkout/**").permitAll()
                    .requestMatchers("/api/orders/**").permitAll()
                    // Static resources
                    .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                    // Public pages
                    .requestMatchers("/", "/catalog", "/products/**", "/cart", "/checkout", "/order-confirmation").permitAll()
                    // All other requests require authentication
                    .anyRequest().authenticated()
            }
            .formLogin { form ->
                form
                    .loginPage("/admin/login")
                    .loginProcessingUrl("/admin/login")
                    .defaultSuccessUrl("/admin/dashboard", true)
                    .failureUrl("/admin/login?error=true")
                    .permitAll()
            }
            .logout { logout ->
                logout
                    .logoutUrl("/admin/logout")
                    .logoutSuccessUrl("/admin/login?logout=true")
                    .permitAll()
            }
            .csrf { csrf ->
                csrf
                    // Enable CSRF protection for admin operations
                    .ignoringRequestMatchers("/api/products/**", "/api/cart/**", "/api/checkout/**", "/api/orders/**")
            }

        return http.build()
    }
}