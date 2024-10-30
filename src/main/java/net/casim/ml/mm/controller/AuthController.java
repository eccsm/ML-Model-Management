package net.casim.ml.mm.controller;

import jakarta.validation.Valid;
import net.casim.ml.mm.data.User;
import net.casim.ml.mm.data.request.AuthRequest;
import net.casim.ml.mm.data.request.RegisterRequest;
import net.casim.ml.mm.service.JwtService;
import net.casim.ml.mm.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final UserService userService;

    public AuthController(PasswordEncoder passwordEncoder, JwtService jwtService, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> authenticateUser(@Valid @RequestBody AuthRequest authRequest) {
        Optional<User> user = userService.getUserByUsername(authRequest.getUsername());

        if (user.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid username or password"));

        if (!passwordEncoder.matches(authRequest.getPassword(), user.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid username or password"));
        }

        String jwtToken = jwtService.generateToken(user.get());

        // Wrap token in an object instead of returning it as plain text
        return ResponseEntity.ok(Map.of("token", jwtToken));
    }


    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User registeredUser = userService.registerUser(registerRequest);
            return ResponseEntity.ok(registeredUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}

