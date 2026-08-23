package com.seedtoserve.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");

		String token = null;
		String username = null;
		String role = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			token = authHeader.substring(7);

			try {
				username = jwtUtil.getUsernameFromToken(token);
				role = jwtUtil.getRoleFromToken(token);

			} catch (Exception e) {
				System.out.println("JWTTokenFilter: Invalid token - " + e.getMessage());
			}
		}

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			UserDetails userDetails;

			// ADMIN
			if ("ADMIN".equalsIgnoreCase(role)) {

				userDetails = adminUserAuthenticationService.loadUserByUsername(username);

			}
			// Existing Customer/Farmer authentication
			else {

				userDetails = userService.loadUserByUsername(username);
			}

			if (jwtUtil.isValidToken(token, userDetails.getUsername())) {

				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities());

				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authToken);

				System.out.println("JWTTokenFilter: Security context set for user " + username);

				System.out.println("User authorities: " + userDetails.getAuthorities());

			} else {

				System.out.println("JWTTokenFilter: Invalid or expired token for user " + username);
			}
		}

		filterChain.doFilter(request, response);
	}

}
