package com.example.bankcards.service;

import com.example.bankcards.entity.CardUser;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.EffectiveUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EffectiveUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CardUser cardUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("There is no user with username '"+username+"'"));
        return new EffectiveUserDetails(cardUser);
    }
}
