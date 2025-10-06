package org.example.eshop.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
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
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

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

        val user: UserDetails = User.builder()
            .username("user")
            .password(passwordEncoder().encode("password"))
            .roles("USER")
            .build()

        return InMemoryUserDetailsManager(admin, user)
    }

    // Admin security chain (higher priority)
    @Bean
    @Order(1)
    fun adminFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher("/admin/**", "/api/admin/**")
            .authorizeHttpRequests { authz ->
                authz
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .requestMatchers("/admin/**").hasRole("ADMIN")
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
                // Keep CSRF enabled for admin operations; do not ignore admin endpoints
                csrf
            }
        return http.build()
    }

    // Public site security chain (fallback)
    @Bean
    @Order(2)
    fun appFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { authz ->
                authz
                    // Public APIs
                    .requestMatchers("/api/products/**", "/api/cart/**", "/api/checkout/**", "/api/orders/**").permitAll()
                    // Static resources
                    .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                    // Public pages
                    .requestMatchers("/", "/catalog", "/products/**", "/cart", "/checkout", "/order-confirmation", "/login").permitAll()
                    // User area
                    .requestMatchers("/account/**").authenticated()
                    // Anything else on public site is allowed
                    .anyRequest().permitAll()
            }
            .formLogin { form ->
                form
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/account", true)
                    .failureUrl("/login?error=true")
                    .permitAll()
            }
            .logout { logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout=true")
                    .permitAll()
            }
            .csrf { csrf ->
                csrf
                    // Allow APIs to be called without CSRF tokens (public client usage)
                    .ignoringRequestMatchers("/api/products/**", "/api/cart/**", "/api/checkout/**", "/api/orders/**")
            }
        return http.build()
    }
}