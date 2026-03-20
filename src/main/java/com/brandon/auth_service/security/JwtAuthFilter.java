package com.brandon.auth_service.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Custom security filter that intercepts every incoming HTTP request to
 * validate JSON Web Tokens.
 * <p>
 * This filter extracts the JWT from the {@code Authorization} header, verifies
 * its validity via {@link JwtService}, and populates the
 * {@link SecurityContextHolder}
 * if the token is valid, effectively authenticating the user for the duration
 * of the request.
 * </p>
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Performs the core filtering logic to authenticate requests via JWT.
     * <p>
     * 1. Checks for a "Bearer " prefix in the Authorization header.<br>
     * 2. Extracts the user identity (Subject) from the token .<br>
     * 3. Loads user details and vaildates the token signature/expiration.<br>
     * 4. Updates the Security Context with an
     * {@link UsernamePasswordAuthenticationToken}.
     * </p>
     * 
     * @param request     The incoming HTTP request.
     * @param response    The otugoing HTTP response.
     * @param filterChain tThe chain of subsequent filters to execute.
     * @throws ServletException If a generic servlet error occurs.
     * @throws IOException      If an I/O error occurs during processing.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userId;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        userId = jwtService.extractUserId(jwt);

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userId);

            if (jwtService.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

}
