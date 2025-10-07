package com.example.bankcards.service;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.CardUser;
import com.example.bankcards.entity.Role;
import com.example.bankcards.exception.UnauthorizedException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.IdGenerator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private CardUser testUser;
    private UserDTO testUserDTO;
    private final String userId = "user123";
    private final String username = "testuser";
    private final String password = "password";
    private final String encodedPassword = "encodedPassword";
    private final String token = "jwt.token.here";

    @BeforeEach
    void setUp() {
        testUser = new CardUser();
        testUser.setId(userId);
        testUser.setUsername(username);
        testUser.setPassword(encodedPassword);
        testUser.setRoles(Set.of(Role.USER));

        testUserDTO = new UserDTO(userId, username, Set.of(Role.USER));
    }

    @Test
    void getAll_ShouldReturnPageOfUsers() {
        // Arrange
        Pageable pageable = Pageable.ofSize(10);
        Page<CardUser> userPage = new PageImpl<>(List.of(testUser));
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // Act
        Page<UserDTO> result = userService.getAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testUserDTO, result.getContent().get(0));
        verify(userRepository).findAll(pageable);
    }

    @Test
    void getByID_WithExistingUser_ShouldReturnUserDTO() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        UserDTO result = userService.getByID(userId);

        // Assert
        assertNotNull(result);
        assertEquals(testUserDTO, result);
        verify(userRepository).findById(userId);
    }

    @Test
    void getByID_WithNonExistingUser_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.getByID(userId)
        );

        assertEquals("Could not fetch user from db: User does not exist", exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    void getToken_WithValidCredentials_ShouldReturnToken() {
        // Arrange
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(jwtService.generateToken(username)).thenReturn(token);

        // Act
        String result = userService.getToken(username, password);

        // Assert
        assertEquals(token, result);
        verify(authManager).authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        verify(jwtService).generateToken(username);
    }

    @Test
    void getToken_WithInvalidCredentials_ShouldThrowUnauthorizedException() {
        // Arrange
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("User is not authenticated. Invalid credentials"));

        // Act & Assert
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> userService.getToken(username, password)
        );

        assertEquals("User is not authenticated. Invalid credentials", exception.getMessage());
        verify(authManager).authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void getToken_WithUnsuccessfulAuthentication_ShouldThrowUnauthorizedException() {
        // Arrange
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(false);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        // Act & Assert
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> userService.getToken(username, password)
        );

        assertEquals("User is not authenticated", exception.getMessage());
        verify(authManager).authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void createUser_ShouldCreateAndReturnUserDTO() {
        // Arrange
        String generatedId = "generated123";
        try (MockedStatic<IdGenerator> idGeneratorMock = mockStatic(IdGenerator.class)) {
            idGeneratorMock.when(IdGenerator::generateId).thenReturn(generatedId);
            when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
            when(userRepository.save(any(CardUser.class))).thenAnswer(invocation -> {
                CardUser user = invocation.getArgument(0);
                user.setId(generatedId);
                return user;
            });

            // Act
            UserDTO result = userService.createUser(username, password);

            // Assert
            assertNotNull(result);
            assertEquals(generatedId, result.id());
            assertEquals(username, result.username());
            assertEquals(Set.of(Role.USER), result.roles());

            idGeneratorMock.verify(IdGenerator::generateId);
            verify(passwordEncoder).encode(password);
            verify(userRepository).save(any(CardUser.class));
        }
    }

    @Test
    void deleteUser_WithExistingUser_ShouldDeleteAndReturnUserDTO() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).deleteById(userId);

        // Act
        UserDTO result = userService.deleteUser(userId);

        // Assert
        assertNotNull(result);
        assertEquals(testUserDTO, result);
        verify(userRepository).findById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_WithNonExistingUser_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.deleteUser(userId)
        );

        assertEquals("No entity with id '" + userId + "'. Entity is not deleted", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(userRepository, never()).deleteById(anyString());
    }

    @Test
    void createUser_ShouldSetCorrectProperties() {
        // Arrange
        String generatedId = "test-id-123";

        // Используем MockedStatic для статического метода
        try (MockedStatic<IdGenerator> idGeneratorMock = mockStatic(IdGenerator.class)) {
            idGeneratorMock.when(IdGenerator::generateId).thenReturn(generatedId);
            when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
            when(userRepository.save(any(CardUser.class))).thenAnswer(invocation ->
                    invocation.getArgument(0)
            );

            // Act
            UserDTO result = userService.createUser(username, password);

            // Assert
            verify(userRepository).save(argThat(user ->
                    user.getId().equals(generatedId) &&
                            user.getUsername().equals(username) &&
                            user.getPassword().equals(encodedPassword) &&
                            user.getRoles().equals(Set.of(Role.USER))
            ));

            assertEquals(generatedId, result.id());
            assertEquals(username, result.username());
            assertEquals(Set.of(Role.USER), result.roles());

            // Проверяем, что статический метод был вызван
            idGeneratorMock.verify(IdGenerator::generateId);
        }
    }
}
