package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByOwnerId(String ownerId);
    Optional<Card> findByCardNumber(String cardNumber);
    @Modifying
    @Transactional
    @Query(value = "UPDATE card SET status = 2 WHERE expiry_date < CURRENT_DATE",
            nativeQuery = true)
    void expireCards();
    Optional<Card> findByIdAndOwnerId(Long id, String ownerId);
}
