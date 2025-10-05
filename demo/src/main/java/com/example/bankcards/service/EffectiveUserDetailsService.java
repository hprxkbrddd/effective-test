package com.example.bankcards.service;

import com.example.bankcards.entity.CardUser;
import com.example.bankcards.entity.Role;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.EffectiveUserDetails;
import com.example.bankcards.util.IdGenerator;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@RequiredArgsConstructor
@Service
public class EffectiveUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Value("${app.admin.username}")
    private String adminUsername;
    @Value("${app.admin.password}")
    private String adminPassword;

    @PostConstruct
    @Transactional
    public void saveAdmin() {
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            CardUser admin = new CardUser();
            admin.setRoles(Set.of(Role.ADMIN));
            admin.setId(IdGenerator.generateId());
            admin.setUsername(adminUsername);
            admin.setPassword(encoder.encode(adminPassword));
            userRepository.save(admin);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CardUser cardUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("There is no user with username '" + username + "'"));
        return new EffectiveUserDetails(cardUser);
    }
}
