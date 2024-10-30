package net.casim.ml.mm.service;

import lombok.extern.slf4j.Slf4j;
import net.casim.ml.mm.data.User;
import net.casim.ml.mm.data.request.RegisterRequest;
import net.casim.ml.mm.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(RegisterRequest request) {
        log.info("Attempting to register user with username: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Registration failed: Username '{}' already exists", request.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }

        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            log.debug("No roles specified for user '{}', defaulting to 'USER'", request.getUsername());
            request.setRoles(List.of("USER"));
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        List<String> roles = new ArrayList<>();
        for (String role : request.getRoles()) {
            String upperRole = role.toUpperCase();
            if (upperRole.equals("ADMIN") || upperRole.equals("USER")) {
                roles.add(upperRole);
            } else {
                log.warn("Invalid role '{}' provided for user '{}'", role, request.getUsername());
                throw new IllegalArgumentException("Invalid role: " + role + ". Only 'ADMIN' or 'USER' roles are allowed.");
            }
        }
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        log.info("User '{}' registered successfully with roles: {}", savedUser.getUsername(), savedUser.getRoles());

        return savedUser;
    }

    public Optional<User> getUserByUsername(String username) {
        log.debug("Fetching user by username: {}", username);
        Optional<User> user = userRepository.findUserByUsername(username);
        if (user.isPresent()) {
            log.debug("User '{}' found", username);
        } else {
            log.debug("User '{}' not found", username);
        }
        return user;
    }
}
