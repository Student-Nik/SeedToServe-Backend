package com.seedtoserve.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SecurityUserAuthenticationService userService;

    @Autowired
    private AdminUserAuthenticationService adminUserAuthenticationService;

    @Autowired
    private DeliveryBoyUserAuthenticationService deliveryBoyUserAuthenticationService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        String token = null;
        String username = null;
        String role = null;

        // =====================================================
        // GET JWT TOKEN
        // =====================================================

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            token = authHeader.substring(7);

            try {

                username = jwtUtil.getUsernameFromToken(token);
                role = jwtUtil.getRoleFromToken(token);

                System.out.println("JWT Username: " + username);
                System.out.println("JWT Role: " + role);

            } catch (Exception e) {

                System.out.println(
                        "JWTTokenFilter: Invalid token - "
                                + e.getMessage());
            }
        }

        // =====================================================
        // AUTHENTICATE USER
        // =====================================================

        if (username != null
                && role != null
                && SecurityContextHolder.getContext()
                        .getAuthentication() == null) {

            UserDetails userDetails;

            // =================================================
            // ADMIN
            // =================================================

            if ("ADMIN".equalsIgnoreCase(role)) {

                userDetails =
                        adminUserAuthenticationService
                                .loadUserByUsername(username);
            }

            // =================================================
            // DELIVERY BOY
            // =================================================

            else if ("DELIVERY_BOY".equalsIgnoreCase(role)) {

                userDetails =
                        deliveryBoyUserAuthenticationService
                                .loadUserByUsername(username);
            }

            // =================================================
            // CUSTOMER / FARMER
            // =================================================

            else {

                userDetails =
                        userService
                                .loadUserByUsername(username);
            }

            // =================================================
            // VALIDATE TOKEN
            // =================================================

            if (jwtUtil.isValidToken(
                    token,
                    userDetails.getUsername())) {

                /*
                 * Spring Security hasRole("DELIVERY_BOY")
                 * expects:
                 *
                 * ROLE_DELIVERY_BOY
                 */

                String authorityName =
                        "ROLE_" + role.toUpperCase();

                List<GrantedAuthority> authorities =
                        List.of(
                                new SimpleGrantedAuthority(
                                        authorityName)
                        );

                // =================================================
                // CREATE AUTHENTICATION
                // =================================================

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                authorities
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authToken);

                System.out.println(
                        "JWTTokenFilter: Security context set for "
                                + username);

                System.out.println(
                        "JWT Role: " + role);

                System.out.println(
                        "Granted Authority: " + authorities);
            }
        }

        filterChain.doFilter(request, response);
    }
}