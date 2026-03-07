package edu.cit.mahinay.kwikq.service;

import edu.cit.mahinay.kwikq.dto.LoginRequest;
import edu.cit.mahinay.kwikq.dto.RegisterRequest;
import edu.cit.mahinay.kwikq.dto.AuthResponse;
import edu.cit.mahinay.kwikq.entity.User;
import edu.cit.mahinay.kwikq.repository.UserRepository;
import edu.cit.mahinay.kwikq.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * Register a new user.
     * - Validates required fields (handled by DTO validation)
     * - Prevents duplicate email registration
     * - Stores password securely (BCrypt hashed)
     */
    public AuthResponse register(RegisterRequest request) {
        // Check for duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        // Create new user with hashed password
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.PATRON); // Default role

        User savedUser = userRepository.save(user);

        // Generate JWT token
        String token = jwtUtils.generateTokenFromEmail(savedUser.getEmail());

        return new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole().name()
        );
    }

    /**
     * Authenticate a user.
     * - Validates credentials against the database
     * - Prevents login with invalid credentials
     * - Returns JWT token on success
     */
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            String token = jwtUtils.generateToken(authentication);

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return new AuthResponse(
                    token,
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole().name()
            );
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid email or password");
        }
    }
}
