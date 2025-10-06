package com.example.bankcards.service;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.CardUser;
import com.example.bankcards.entity.Role;
import com.example.bankcards.exception.UnauthorizedException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.IdGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public Page<UserDTO> getAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(CardUser::toDTO);
    }

    public UserDTO getByID(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Could not fetch user from db: User does not exist"))
                .toDTO();
    }

    public String getToken(String username, String password) {
        Authentication auth =
                authManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                username,
                                password
                        )
                );
        if (auth.isAuthenticated()) {
            return jwtService.generateToken(username);
        } else {
            throw new UnauthorizedException("User is not authenticated");
        }
    }

    public UserDTO createUser(String username, String password) {
        CardUser cardUser = new CardUser();
        cardUser.setId(IdGenerator.generateId());
        cardUser.setUsername(username);
        cardUser.setRoles(Set.of(Role.USER));
        cardUser.setPassword(passwordEncoder.encode(password));
        userRepository.save(cardUser);
        return cardUser.toDTO();
    }

    public UserDTO deleteUser(String id) {
        UserDTO userDTO = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No entity with id '" + id + "'. Entity is not deleted"))
                .toDTO();
        userRepository.deleteById(id);
        return userDTO;
    }
}
