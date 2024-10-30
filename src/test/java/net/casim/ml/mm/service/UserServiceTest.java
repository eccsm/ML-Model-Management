package net.casim.ml.mm.service;

import net.casim.ml.mm.data.User;
import net.casim.ml.mm.data.request.RegisterRequest;
import net.casim.ml.mm.repository.UserRepository;
import net.casim.ml.mm.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterRequest request;
    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        request = new RegisterRequest();
        request.setUsername("testUser");
        request.setPassword("password");
        request.setRoles(List.of("USER"));

        user = new User();
        user.setUsername("testUser");
        user.setRoles(List.of("USER"));
    }

    @Test
    public void testRegisterUserSuccess() {
        when(userRepository.existsByUsername("testUser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User registeredUser = userService.registerUser(request);

        assertNotNull(registeredUser);
        assertEquals("testUser", registeredUser.getUsername());
        assertEquals(List.of("USER"), registeredUser.getRoles());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testRegisterUserDuplicateUsername() {
        when(userRepository.existsByUsername("testUser")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.registerUser(request));
        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void testRegisterUserInvalidRole() {
        request.setRoles(List.of("INVALID_ROLE"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.registerUser(request));
        assertEquals("Invalid role: INVALID_ROLE. Only 'ADMIN' or 'USER' roles are allowed.", exception.getMessage());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void testGetUserByUsernameFound() {
        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserByUsername("testUser");

        assertTrue(foundUser.isPresent());
        assertEquals("testUser", foundUser.get().getUsername());
        verify(userRepository, times(1)).findUserByUsername("testUser");
    }

    @Test
    public void testGetUserByUsernameNotFound() {
        when(userRepository.findUserByUsername("unknownUser")).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.getUserByUsername("unknownUser");

        assertFalse(foundUser.isPresent());
        verify(userRepository, times(1)).findUserByUsername("unknownUser");
    }
}
