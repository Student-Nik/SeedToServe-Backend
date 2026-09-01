package com.seedtoserve.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.seedtoserve.config.GoogleSuccessHandler;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Autowired
    private GoogleSuccessHandler googleSuccessHandler;


    // =====================================================
    // PASSWORD ENCODER
    // =====================================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // =====================================================
    // CUSTOMER AUTHENTICATION PROVIDER
    // =====================================================

    @Bean
    public AuthenticationProvider customerAuthenticationProvider(
            SecurityUserAuthenticationService customerUserDetails,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(customerUserDetails);
        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }


    // =====================================================
    // CUSTOMER AUTHENTICATION MANAGER
    // =====================================================

    @Bean
    @Primary
    public AuthenticationManager customerAuthenticationManager(
            @Qualifier("customerAuthenticationProvider")
            AuthenticationProvider customerAuthenticationProvider) {

        return new ProviderManager(
                customerAuthenticationProvider);
    }


    // =====================================================
    // ADMIN AUTHENTICATION PROVIDER
    // =====================================================

    @Bean
    public AuthenticationProvider adminAuthenticationProvider(
            AdminUserAuthenticationService adminUserDetails,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(adminUserDetails);
        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }


    // =====================================================
    // ADMIN AUTHENTICATION MANAGER
    // =====================================================

    @Bean
    public AuthenticationManager adminAuthenticationManager(
            @Qualifier("adminAuthenticationProvider")
            AuthenticationProvider adminAuthenticationProvider) {

        return new ProviderManager(
                adminAuthenticationProvider);
    }


    // =====================================================
    // DELIVERY BOY AUTHENTICATION PROVIDER
    // =====================================================

    @Bean
    public AuthenticationProvider deliveryBoyAuthenticationProvider(
            DeliveryBoyUserAuthenticationService deliveryBoyUserDetails,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(deliveryBoyUserDetails);
        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }


    // =====================================================
    // DELIVERY BOY AUTHENTICATION MANAGER
    // =====================================================

    @Bean
    public AuthenticationManager deliveryBoyAuthenticationManager(
            @Qualifier("deliveryBoyAuthenticationProvider")
            AuthenticationProvider deliveryBoyAuthenticationProvider) {

        return new ProviderManager(
                deliveryBoyAuthenticationProvider);
    }


    // =====================================================
    // SECURITY FILTER CHAIN
    // =====================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            @Qualifier("customerAuthenticationManager")
            AuthenticationManager authenticationManager)
            throws Exception {

        http
                // =================================================
                // CSRF
                // =================================================

                .csrf(csrf -> csrf.disable())


                // =================================================
                // CORS
                // =================================================

                .cors(cors -> cors.configurationSource(request -> {

                    CorsConfiguration config =
                            new CorsConfiguration();

                    config.setAllowedOrigins(
                            List.of("http://localhost:5173"));

                    config.setAllowedMethods(
                            List.of(
                                    "GET",
                                    "POST",
                                    "PUT",
                                    "DELETE",
                                    "OPTIONS"
                            ));

                    config.setAllowedHeaders(
                            List.of("*"));

                    config.setAllowCredentials(true);

                    return config;
                }))


                // =================================================
                // AUTHORIZATION
                // =================================================

                .authorizeHttpRequests(auth -> auth

                        // PUBLIC APIs
                        .requestMatchers(
                                "/api/auth/**",
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/error",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/contact/**",

                                // Admin login
                                "/api/admin/login",

                                // Delivery boy login
                                "/api/delivery/boy/login",

                                // Public products/categories
                                "/api/farmer/products/show/products",
                                "/api/farmer/categories/show/categories"
                        )
                        .permitAll()


                        // FARMER APIs
                        .requestMatchers("/api/farmer/**")
                        .hasRole("FARMER")


                        // BUYER APIs
                        .requestMatchers("/api/buyer/**")
                        .hasRole("BUYER")


                        // ADMIN APIs
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")


                        // DELIVERY BOY APIs
                        .requestMatchers("/api/delivery/boy/orders/**")
                        .hasRole("DELIVERY_BOY")


                        // EVERYTHING ELSE
                        .anyRequest()
                        .authenticated()
                )


                // =================================================
                // GOOGLE LOGIN
                // =================================================

                .oauth2Login(oauth ->
                        oauth.successHandler(
                                googleSuccessHandler))


                // =================================================
                // SESSION
                // =================================================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.IF_REQUIRED))


                // =================================================
                // DEFAULT AUTHENTICATION MANAGER
                // =================================================

                .authenticationManager(authenticationManager);


        // =====================================================
        // JWT FILTER
        // =====================================================

        http.addFilterBefore(
                jwtTokenFilter,
                UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}