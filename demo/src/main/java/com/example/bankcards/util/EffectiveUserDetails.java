package com.example.bankcards.util;

import com.example.bankcards.entity.CardUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class EffectiveUserDetails implements UserDetails {

    private final CardUser cardUser;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return cardUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return cardUser.getPassword();
    }

    @Override
    public String getUsername() {
        return cardUser.getUsername();
    }
}
