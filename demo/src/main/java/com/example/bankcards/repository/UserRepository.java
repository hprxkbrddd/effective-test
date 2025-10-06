package com.example.bankcards.repository;

import com.example.bankcards.entity.CardUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;
import org.springframework.lang.NonNullFields;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<CardUser, String> {

    @NonNull
    Page<CardUser> findAll(@NonNull Pageable pageable);
    Optional<CardUser> findByUsername(String username);
}
