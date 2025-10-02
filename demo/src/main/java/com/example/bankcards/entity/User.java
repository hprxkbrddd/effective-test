package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@RequiredArgsConstructor
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    private final String id;
    private String username;
    private String password;
    private Set<Role> roles;
}
