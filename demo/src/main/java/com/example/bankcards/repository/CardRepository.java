package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Queue;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Page<Card> findAll(Pageable pageable);

    Page<Card> findByOwnerId(String ownerId, Pageable pageable);

    Optional<Card> findByCardNumber(String cardNumber);

    Optional<Card> findByIdAndOwnerId(Long id, String ownerId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE card SET status = 2 WHERE expiry_date < CURRENT_DATE",
            nativeQuery = true)
    void expireCards();

    @Modifying
    @Transactional
    @Query(value = "UPDATE card SET status = 1 WHERE id IN :cardIds",
            nativeQuery = true)
    void blockCards(Queue<Long> cardIds);

    @Modifying
    @Transactional
    @Query(value = "UPDATE card SET balance = balance + :amount WHERE id = :cardId",
            nativeQuery = true)
    int deposit(Long cardId, BigDecimal amount);

    @Modifying
    @Transactional
    @Query(value = "UPDATE card SET balance = balance - :amount WHERE id = :cardId AND balance >= :amount",
            nativeQuery = true)
    int withdraw(@Param("cardId") Long cardId, @Param("amount") BigDecimal amount);

    @Query(value = "SELECT transfer_funds(:fromId, :toId, :amount)", nativeQuery = true)
    boolean transfer(Long fromId, Long toId, BigDecimal amount);
}
