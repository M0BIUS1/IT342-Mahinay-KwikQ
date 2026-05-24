package edu.cit.mahinay.kwikq.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import edu.cit.mahinay.kwikq.features.users.repository.UserRepository;
import edu.cit.mahinay.kwikq.features.users.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private SupabaseJwtValidator supabaseJwtValidator;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null) {
                // Try local JWT first
                if (jwtUtils.validateToken(jwt)) {
                    String email = jwtUtils.getEmailFromToken(jwt);
                    // Prefer using the persisted User entity as the principal so existing
                    // code that casts SecurityContext principal to `User` continues to work.
                    User userEntity = null;
                    try {
                        userEntity = userRepository.findByEmail(email).orElse(null);
                    } catch (Exception ignored) {}

                    // Try to load UserDetails, but tolerate missing users (don't throw)
                    UserDetails userDetails = null;
                    try {
                        userDetails = userDetailsService.loadUserByUsername(email);
                    } catch (UsernameNotFoundException ex) {
                        // User not found in local user store; we'll fall back to a transient auth
                    } catch (Exception ex) {
                        // Other errors while loading userDetails should not abort the request
                    }

                    Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
                    if (userDetails != null) {
                        authorities = userDetails.getAuthorities();
                    } else {
                        // Default to a basic role when we don't have UserDetails
                        ((ArrayList<GrantedAuthority>) authorities).add(new SimpleGrantedAuthority("ROLE_USER"));
                    }

                    UsernamePasswordAuthenticationToken authentication;
                    if (userEntity != null) {
                        authentication = new UsernamePasswordAuthenticationToken(
                                userEntity, null, authorities);
                    } else if (userDetails != null) {
                        authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, authorities);
                    } else {
                        // Fallback: use the email string as principal for downstream checks
                        authentication = new UsernamePasswordAuthenticationToken(
                                email, null, authorities);
                    }
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } 
                // Try Supabase JWT if local JWT validation failed
                else if (supabaseJwtValidator.validateToken(jwt)) {
                    String email = supabaseJwtValidator.getEmailFromToken(jwt);
                    
                    if (email != null && !email.isEmpty()) {
                        // Try to load user from database, or create a transient user for Supabase tokens
                        try {
                            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                            User userEntity = null;
                            try {
                                userEntity = userRepository.findByEmail(email).orElse(null);
                            } catch (Exception ignored) {}

                            UsernamePasswordAuthenticationToken authentication;
                            if (userEntity != null) {
                                authentication = new UsernamePasswordAuthenticationToken(
                                        userEntity, null, userDetails.getAuthorities());
                            } else {
                                authentication = new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());
                            }
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        } catch (Exception e) {
                            // If user not found in database, create temporary authentication with basic role
                            Collection<GrantedAuthority> authorities = new ArrayList<>();
                            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                            
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(
                                            email, null, authorities);
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication", e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
