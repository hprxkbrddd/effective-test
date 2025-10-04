package com.example.bankcards.repository;

import com.example.bankcards.entity.CardUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<CardUser, String> {
    Optional<CardUser> findByUsername(String username);
}
