package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CardType;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.CardUser;
import com.example.bankcards.exception.BalanceException;
import com.example.bankcards.exception.CardPropertyNotAccessibleException;
import com.example.bankcards.exception.InvalidCardException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardNumberGenerator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardNumberGenerator cardNumberGenerator;

    @InjectMocks
    private CardService cardService;

    private Card activeCard;
    private Card blockedCard;
    private Card expiredCard;
    private final String ownerId = "user123";
    private final Long cardId = 1L;
    private final String cardNumber1 = "1234567890123456";
    private final String cardNumber2 = "8765432187654321";
    private final String cardNumber3 = "1122334455667788";

    private final String NON_EXISTENT_USER_ID = "nonExistentUser";
    private final Pageable PAGEABLE = PageRequest.of(0, 10);

    @BeforeEach
    void setUp() {
        activeCard = new Card(cardNumber1, ownerId);
        activeCard.setId(cardId);
        activeCard.setStatus(CardStatus.ACTIVE);
        activeCard.setBalance(BigDecimal.valueOf(1000));

        blockedCard = new Card(cardNumber1, ownerId);
        blockedCard.setId(2L);
        blockedCard.setStatus(CardStatus.BLOCKED);

        expiredCard = new Card(cardNumber1, ownerId);
        expiredCard.setId(3L);
        expiredCard.setStatus(CardStatus.EXPIRED);
    }

    @Test
    void getActiveCardsOfUser_WhenUserExists_ShouldReturnActiveCards() {
        // Arrange
        CardUser user = new CardUser();
        user.setId(ownerId);

        Card activeCard1 = createCard(1L, ownerId, CardStatus.ACTIVE, cardNumber1);
        Card activeCard2 = createCard(2L, ownerId, CardStatus.ACTIVE, cardNumber2);
        List<Card> activeCards = Arrays.asList(activeCard1, activeCard2);
        Page<Card> activeCardsPage = new PageImpl<>(activeCards, PAGEABLE, activeCards.size());

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(cardRepository.findByOwnerIdAndStatus(ownerId, CardStatus.ACTIVE, PAGEABLE))
                .thenReturn(activeCardsPage);

        // Act
        Page<CardDTO> result = cardService.getActiveCardsOfUser(ownerId, PAGEABLE);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(activeCard1.toDTOEncrypted(), result.getContent().get(0));
        assertEquals(activeCard2.toDTOEncrypted(), result.getContent().get(1));
    }

    @Test
    void getActiveCardsOfUser_WhenUserNotExists_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(NON_EXISTENT_USER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> cardService.getActiveCardsOfUser(NON_EXISTENT_USER_ID, PAGEABLE));

        assertEquals("Could not fetch cards of user. User is not in database", exception.getMessage());
    }

    @Test
    void getActiveCardsOfUser_WhenNoActiveCards_ShouldReturnEmptyPage() {
        // Arrange
        CardUser user = new CardUser();
        user.setId(ownerId);
        Page<Card> emptyPage = new PageImpl<>(List.of(), PAGEABLE, 0);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(cardRepository.findByOwnerIdAndStatus(ownerId, CardStatus.ACTIVE, PAGEABLE))
                .thenReturn(emptyPage);

        // Act
        Page<CardDTO> result = cardService.getActiveCardsOfUser(ownerId, PAGEABLE);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void getBlockedCardsOfUser_WhenUserExists_ShouldReturnBlockedCards() {
        // Arrange
        CardUser user = new CardUser();
        user.setId(ownerId);

        Card blockedCard1 = createCard(1L, ownerId, CardStatus.BLOCKED, cardNumber1);
        Card blockedCard2 = createCard(2L, ownerId, CardStatus.BLOCKED, cardNumber2);
        List<Card> blockedCards = Arrays.asList(blockedCard1, blockedCard2);
        Page<Card> blockedCardsPage = new PageImpl<>(blockedCards, PAGEABLE, blockedCards.size());

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(cardRepository.findByOwnerIdAndStatus(ownerId, CardStatus.BLOCKED, PAGEABLE))
                .thenReturn(blockedCardsPage);

        // Act
        Page<CardDTO> result = cardService.getBlockedCardsOfUser(ownerId, PAGEABLE);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(blockedCard1.toDTOEncrypted(), result.getContent().get(0));
        assertEquals(blockedCard2.toDTOEncrypted(), result.getContent().get(1));
    }

    @Test
    void getBlockedCardsOfUser_WhenUserNotExists_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(NON_EXISTENT_USER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> cardService.getBlockedCardsOfUser(NON_EXISTENT_USER_ID, PAGEABLE));

        assertEquals("Could not fetch cards of user. User is not in database", exception.getMessage());
    }

    @Test
    void getExpiredCardsOfUser_WhenUserExists_ShouldReturnExpiredCards() {
        // Arrange
        CardUser user = new CardUser();
        user.setId(ownerId);

        Card expiredCard1 = createCard(1L, ownerId, CardStatus.EXPIRED, cardNumber1);
        Card expiredCard2 = createCard(2L, ownerId, CardStatus.EXPIRED, cardNumber2);
        List<Card> expiredCards = Arrays.asList(expiredCard1, expiredCard2);
        Page<Card> expiredCardsPage = new PageImpl<>(expiredCards, PAGEABLE, expiredCards.size());

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(cardRepository.findByOwnerIdAndStatus(ownerId, CardStatus.EXPIRED, PAGEABLE))
                .thenReturn(expiredCardsPage);

        // Act
        Page<CardDTO> result = cardService.getExpiredCardsOfUser(ownerId, PAGEABLE);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(expiredCard1.toDTOEncrypted(), result.getContent().get(0));
        assertEquals(expiredCard2.toDTOEncrypted(), result.getContent().get(1));
    }

    @Test
    void getExpiredCardsOfUser_WhenUserNotExists_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(NON_EXISTENT_USER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> cardService.getExpiredCardsOfUser(NON_EXISTENT_USER_ID, PAGEABLE));

        assertEquals("Could not fetch cards of user. User is not in database", exception.getMessage());
    }

    @Test
    void getAllMethods_ShouldUseCorrectCardStatus() {
        // Arrange
        CardUser user = new CardUser();
        user.setId(ownerId);

        Card activeCard = createCard(1L, ownerId, CardStatus.ACTIVE, cardNumber1);
        Card blockedCard = createCard(2L, ownerId, CardStatus.BLOCKED, cardNumber2);
        Card expiredCard = createCard(3L, ownerId, CardStatus.EXPIRED, cardNumber3);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));

        // Test active cards
        when(cardRepository.findByOwnerIdAndStatus(ownerId, CardStatus.ACTIVE, PAGEABLE))
                .thenReturn(new PageImpl<>(List.of(activeCard)));
        Page<CardDTO> activeResult = cardService.getActiveCardsOfUser(ownerId, PAGEABLE);
        assertEquals(1, activeResult.getContent().size());
        assertEquals(activeCard.toDTOEncrypted(), activeResult.getContent().get(0));

        // Test blocked cards
        when(cardRepository.findByOwnerIdAndStatus(ownerId, CardStatus.BLOCKED, PAGEABLE))
                .thenReturn(new PageImpl<>(List.of(blockedCard)));
        Page<CardDTO> blockedResult = cardService.getBlockedCardsOfUser(ownerId, PAGEABLE);
        assertEquals(1, blockedResult.getContent().size());
        assertEquals(blockedCard.toDTOEncrypted(), blockedResult.getContent().get(0));

        // Test expired cards
        when(cardRepository.findByOwnerIdAndStatus(ownerId, CardStatus.EXPIRED, PAGEABLE))
                .thenReturn(new PageImpl<>(List.of(expiredCard)));
        Page<CardDTO> expiredResult = cardService.getExpiredCardsOfUser(ownerId, PAGEABLE);
        assertEquals(1, expiredResult.getContent().size());
        assertEquals(expiredCard.toDTOEncrypted(), expiredResult.getContent().get(0));
    }

    @Test
    void getActiveCardsOfUser_WithPagination_ShouldPassCorrectPageable() {
        // Arrange
        CardUser user = new CardUser();
        user.setId(ownerId);
        Pageable customPageable = PageRequest.of(2, 5); // page 2, size 5

        Card activeCard = createCard(1L, ownerId, CardStatus.ACTIVE, cardNumber1);
        Page<Card> singleCardPage = new PageImpl<>(List.of(activeCard), customPageable, 1);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(cardRepository.findByOwnerIdAndStatus(ownerId, CardStatus.ACTIVE, customPageable))
                .thenReturn(singleCardPage);

        // Act
        Page<CardDTO> result = cardService.getActiveCardsOfUser(ownerId, customPageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(2, result.getPageable().getPageNumber());
        assertEquals(5, result.getPageable().getPageSize());
    }

    private Card createCard(Long cardId, String ownerId, CardStatus status, String cardNumber1) {
        Card card = new Card();
        card.setId(cardId);
        card.setOwnerId(ownerId);
        card.setStatus(status);
        card.setCardNumber(cardNumber1);
        // Дополнительные поля карты при необходимости
        return card;
    }

    @Test
    void getAll_ShouldReturnPageOfCards() {
        // Arrange
        Pageable pageable = Pageable.unpaged();
        List<Card> cards = Arrays.asList(activeCard, blockedCard);
        Page<Card> cardPage = new PageImpl<>(cards);
        when(cardRepository.findAll(pageable)).thenReturn(cardPage);

        // Act
        Page<CardDTO> result = cardService.getAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(cardRepository).findAll(pageable);
    }

    @Test
    void getById_WhenCardExists_ShouldReturnCardDTO() {
        // Arrange
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(activeCard));

        // Act
        CardDTO result = cardService.getById(cardId);

        // Assert
        assertNotNull(result);
        verify(cardRepository).findById(cardId);
    }

    @Test
    void getById_WhenCardNotExists_ShouldThrowException() {
        // Arrange
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> cardService.getById(cardId));
        verify(cardRepository).findById(cardId);
    }

    @Test
    void getByNumber_WhenCardExists_ShouldReturnCardDTO() {
        // Arrange
        when(cardRepository.findByCardNumber(cardNumber1)).thenReturn(Optional.of(activeCard));

        // Act
        CardDTO result = cardService.getByNumber(cardNumber1);

        // Assert
        assertNotNull(result);
        verify(cardRepository).findByCardNumber(cardNumber1);
    }

    @Test
    void getByNumber_WhenCardNotExists_ShouldThrowException() {
        // Arrange
        when(cardRepository.findByCardNumber(cardNumber1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> cardService.getByNumber(cardNumber1));
        verify(cardRepository).findByCardNumber(cardNumber1);
    }

    @Test
    void getCardsOfUser_WhenUserExists_ShouldReturnPageOfCards() {
        // Arrange
        Pageable pageable = Pageable.unpaged();
        List<Card> cards = Arrays.asList(activeCard);
        Page<Card> cardPage = new PageImpl<>(cards);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(mock(com.example.bankcards.entity.CardUser.class)));
        when(cardRepository.findByOwnerId(ownerId, pageable)).thenReturn(cardPage);

        // Act
        Page<CardDTO> result = cardService.getCardsOfUser(ownerId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository).findById(ownerId);
        verify(cardRepository).findByOwnerId(ownerId, pageable);
    }

    @Test
    void getCardsOfUser_WhenUserNotExists_ShouldThrowException() {
        // Arrange
        Pageable pageable = Pageable.unpaged();
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> cardService.getCardsOfUser(ownerId, pageable));
        verify(userRepository).findById(ownerId);
        verify(cardRepository, never()).findByOwnerId(anyString(), any(Pageable.class));
    }

    @Test
    void create_ShouldCreateNewCard() {
        // Arrange
        String generatedCardNumber = "9876543210987654";
        when(cardNumberGenerator.generateCardNumber(CardType.RANDOM)).thenReturn(generatedCardNumber);
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CardDTO result = cardService.create(ownerId);

        // Assert
        assertNotNull(result);
        verify(cardNumberGenerator).generateCardNumber(CardType.RANDOM);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void setCardStatus_WhenCardExists_ShouldUpdateStatus() {
        // Arrange
        CardStatus newStatus = CardStatus.BLOCKED;
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(activeCard));
        // Убрали ненужный стаббинг для cardRepository.save

        // Act
        CardDTO result = cardService.setCardStatus(cardId, newStatus);

        // Assert
        assertNotNull(result);
        assertEquals(CardStatus.BLOCKED, activeCard.getStatus()); // Проверяем, что статус изменился
        verify(cardRepository).findById(cardId);
    }

    @Test
    void setCardStatus_WhenCardNotExists_ShouldThrowException() {
        // Arrange
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> cardService.setCardStatus(cardId, CardStatus.BLOCKED));
        verify(cardRepository).findById(cardId);
    }

    @Test
    void addToBlockQueue_WhenCardIsActive_ShouldAddToQueue() {
        // Arrange
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(activeCard));

        // Act
        cardService.addToBlockQueue(cardId);

        // Assert
        verify(cardRepository).findById(cardId);
    }

    @Test
    void addToBlockQueue_WhenCardIsBlocked_ShouldThrowException() {
        // Arrange
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(blockedCard));

        // Act & Assert
        assertThrows(InvalidCardException.class, () -> cardService.addToBlockQueue(cardId));
        verify(cardRepository).findById(cardId);
    }

    @Test
    void addToBlockQueue_WhenCardIsExpired_ShouldThrowException() {
        // Arrange
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(expiredCard));

        // Act & Assert
        assertThrows(InvalidCardException.class, () -> cardService.addToBlockQueue(cardId));
        verify(cardRepository).findById(cardId);
    }

    @Test
    void addToBlockQueue_WhenCardNotExists_ShouldThrowException() {
        // Arrange
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> cardService.addToBlockQueue(cardId));
        verify(cardRepository).findById(cardId);
    }

    @Test
    void blockAllRequested_ShouldCallRepositoryMethod() {
        // Act
        cardService.blockAllRequested();

        // Assert
        verify(cardRepository).blockCards(any());
    }

    @Test
    void expire_ShouldCallRepositoryMethod() {
        // Act
        cardService.expire();

        // Assert
        verify(cardRepository).expireCards();
    }

    @Test
    void getBalance_WhenCardBelongsToUser_ShouldReturnBalance() {
        // Arrange
        BigDecimal expectedBalance = BigDecimal.valueOf(1000);
        when(cardRepository.findByIdAndOwnerId(cardId, ownerId)).thenReturn(Optional.of(activeCard));

        // Act
        BigDecimal result = cardService.getBalance(cardId, ownerId);

        // Assert
        assertEquals(expectedBalance, result);
        verify(cardRepository).findByIdAndOwnerId(cardId, ownerId);
    }

    @Test
    void getBalance_WhenCardNotBelongsToUser_ShouldThrowException() {
        // Arrange
        when(cardRepository.findByIdAndOwnerId(cardId, ownerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CardPropertyNotAccessibleException.class, () -> cardService.getBalance(cardId, ownerId));
        verify(cardRepository).findByIdAndOwnerId(cardId, ownerId);
    }

    @Test
    void deposit_WhenCardIsActive_ShouldDepositFunds() {
        // Arrange
        BigDecimal amount = BigDecimal.valueOf(500);
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(activeCard));
        when(cardRepository.deposit(cardId, amount)).thenReturn(1);

        // Act
        cardService.deposit(cardId, amount);

        // Assert
        verify(cardRepository).findById(cardId);
        verify(cardRepository).deposit(cardId, amount);
    }

    @Test
    void deposit_WhenCardIsInvalid_ShouldThrowException() {
        // Arrange
        BigDecimal amount = BigDecimal.valueOf(500);
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(blockedCard));

        // Act & Assert
        assertThrows(InvalidCardException.class, () -> cardService.deposit(cardId, amount));
        verify(cardRepository).findById(cardId);
        verify(cardRepository, never()).deposit(anyLong(), any(BigDecimal.class));
    }

    @Test
    void deposit_WhenCardNotExists_ShouldThrowException() {
        // Arrange
        BigDecimal amount = BigDecimal.valueOf(500);
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> cardService.deposit(cardId, amount));
        verify(cardRepository).findById(cardId);
        verify(cardRepository, never()).deposit(anyLong(), any(BigDecimal.class));
    }

    @Test
    void deposit_WhenRepositoryReturnsZero_ShouldThrowException() {
        // Arrange
        BigDecimal amount = BigDecimal.valueOf(500);
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(activeCard));
        when(cardRepository.deposit(cardId, amount)).thenReturn(0);

        // Act & Assert
        assertThrows(CardPropertyNotAccessibleException.class, () -> cardService.deposit(cardId, amount));
        verify(cardRepository).findById(cardId);
        verify(cardRepository).deposit(cardId, amount);
    }

    @Test
    void withdraw_WhenCardIsActiveAndHasSufficientBalance_ShouldWithdrawFunds() {
        // Arrange
        BigDecimal amount = BigDecimal.valueOf(500);
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(activeCard));
        when(cardRepository.withdraw(cardId, amount)).thenReturn(1);

        // Act
        cardService.withdraw(cardId, amount);

        // Assert
        verify(cardRepository).findById(cardId);
        verify(cardRepository).withdraw(cardId, amount);
    }

    @Test
    void withdraw_WhenCardIsInvalid_ShouldThrowException() {
        // Arrange
        BigDecimal amount = BigDecimal.valueOf(500);
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(blockedCard));

        // Act & Assert
        assertThrows(InvalidCardException.class, () -> cardService.withdraw(cardId, amount));
        verify(cardRepository).findById(cardId);
        verify(cardRepository, never()).withdraw(anyLong(), any(BigDecimal.class));
    }

    @Test
    void withdraw_WhenRepositoryReturnsZero_ShouldThrowException() {
        // Arrange
        BigDecimal amount = BigDecimal.valueOf(500);
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(activeCard));
        when(cardRepository.withdraw(cardId, amount)).thenReturn(0);

        // Act & Assert
        assertThrows(BalanceException.class, () -> cardService.withdraw(cardId, amount));
        verify(cardRepository).findById(cardId);
        verify(cardRepository).withdraw(cardId, amount);
    }

    @Test
    void transfer_WhenBothCardsActiveAndSufficientBalance_ShouldTransferFunds() {
        // Arrange
        Long fromId = 1L;
        Long toId = 2L;
        BigDecimal amount = BigDecimal.valueOf(500);

        // Используем разные ID для from и to карт
        Card fromCard = new Card("1111", ownerId);
        fromCard.setId(fromId);
        fromCard.setStatus(CardStatus.ACTIVE);

        Card toCard = new Card("2222", ownerId);
        toCard.setId(toId);
        toCard.setStatus(CardStatus.ACTIVE);

        when(cardRepository.findById(fromId)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toId)).thenReturn(Optional.of(toCard));
        when(cardRepository.transfer(fromId, toId, amount)).thenReturn(true);

        // Act
        cardService.transfer(fromId, toId, amount);

        // Assert
        verify(cardRepository).findById(fromId);
        verify(cardRepository).findById(toId);
        verify(cardRepository).transfer(fromId, toId, amount);
    }

    @Test
    void transfer_WhenFromCardIsBlocked_ShouldThrowInvalidCardException() {
        // Arrange
        Long fromId = 1L;
        Long toId = 2L;
        BigDecimal amount = BigDecimal.valueOf(500);

        Card blockedFromCard = new Card("1111", ownerId);
        blockedFromCard.setId(fromId);
        blockedFromCard.setStatus(CardStatus.BLOCKED);

        when(cardRepository.findById(fromId)).thenReturn(Optional.of(blockedFromCard));
        // Не нужно заглушать findById(toId) - исключение выбросится при первой же проверке

        // Act & Assert
        InvalidCardException exception = assertThrows(InvalidCardException.class,
                () -> cardService.transfer(fromId, toId, amount));

        assertTrue(exception.getMessage().contains("is blocked or expired"));
        verify(cardRepository, never()).transfer(anyLong(), anyLong(), any(BigDecimal.class));
    }

    @Test
    void transfer_WhenToCardIsExpired_ShouldThrowInvalidCardException() {
        // Arrange
        Long fromId = 1L;
        Long toId = 2L;
        BigDecimal amount = BigDecimal.valueOf(500);

        Card activeFromCard = new Card("1111", ownerId);
        activeFromCard.setId(fromId);
        activeFromCard.setStatus(CardStatus.ACTIVE);

        Card expiredToCard = new Card("2222", ownerId);
        expiredToCard.setId(toId);
        expiredToCard.setStatus(CardStatus.EXPIRED);

        when(cardRepository.findById(fromId)).thenReturn(Optional.of(activeFromCard));
        when(cardRepository.findById(toId)).thenReturn(Optional.of(expiredToCard));

        // Act & Assert
        InvalidCardException exception = assertThrows(InvalidCardException.class,
                () -> cardService.transfer(fromId, toId, amount));

        assertTrue(exception.getMessage().contains("is blocked or expired"));
        verify(cardRepository, never()).transfer(anyLong(), anyLong(), any(BigDecimal.class));
    }

    @Test
    void transfer_WhenBothCardsAreInvalid_ShouldThrowInvalidCardException() {
        // Arrange
        Long fromId = 1L;
        Long toId = 2L;
        BigDecimal amount = BigDecimal.valueOf(500);

        Card blockedFromCard = new Card("1111", ownerId);
        blockedFromCard.setId(fromId);
        blockedFromCard.setStatus(CardStatus.BLOCKED);

        // Только одна заглушка - для from карты
        when(cardRepository.findById(fromId)).thenReturn(Optional.of(blockedFromCard));

        // Act & Assert
        InvalidCardException exception = assertThrows(InvalidCardException.class,
                () -> cardService.transfer(fromId, toId, amount));

        assertTrue(exception.getMessage().contains("is blocked or expired"));
        verify(cardRepository, never()).transfer(anyLong(), anyLong(), any(BigDecimal.class));
    }

    @Test
    void transfer_WhenFromCardDoesNotExist_ShouldThrowEntityNotFoundException() {
        // Arrange
        Long fromId = 1L;
        Long toId = 2L;
        BigDecimal amount = BigDecimal.valueOf(500);

        when(cardRepository.findById(fromId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> cardService.transfer(fromId, toId, amount));

        verify(cardRepository, never()).transfer(anyLong(), anyLong(), any(BigDecimal.class));
    }

    @Test
    void transfer_WhenToCardDoesNotExist_ShouldThrowEntityNotFoundException() {
        // Arrange
        Long fromId = 1L;
        Long toId = 2L;
        BigDecimal amount = BigDecimal.valueOf(500);

        Card activeFromCard = new Card("1111", ownerId);
        activeFromCard.setId(fromId);
        activeFromCard.setStatus(CardStatus.ACTIVE);

        when(cardRepository.findById(fromId)).thenReturn(Optional.of(activeFromCard));
        when(cardRepository.findById(toId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> cardService.transfer(fromId, toId, amount));

        verify(cardRepository, never()).transfer(anyLong(), anyLong(), any(BigDecimal.class));
    }
    //

    @Test
    void transfer_WhenRepositoryReturnsFalse_ShouldThrowException() {
        // Arrange
        Long fromId = 1L;
        Long toId = 2L;
        BigDecimal amount = BigDecimal.valueOf(500);

        Card fromCard = new Card("1111", ownerId);
        fromCard.setId(fromId);
        fromCard.setStatus(CardStatus.ACTIVE);

        Card toCard = new Card("2222", ownerId);
        toCard.setId(toId);
        toCard.setStatus(CardStatus.ACTIVE);

        when(cardRepository.findById(fromId)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toId)).thenReturn(Optional.of(toCard));
        when(cardRepository.transfer(fromId, toId, amount)).thenReturn(false);

        // Act & Assert
        assertThrows(BalanceException.class, () -> cardService.transfer(fromId, toId, amount));
        verify(cardRepository).findById(fromId);
        verify(cardRepository).findById(toId);
        verify(cardRepository).transfer(fromId, toId, amount);
    }

    @Test
    void delete_WhenCardExists_ShouldDeleteAndReturnCardDTO() {
        // Arrange
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(activeCard));

        // Act
        CardDTO result = cardService.delete(cardId);

        // Assert
        assertNotNull(result);
        verify(cardRepository).findById(cardId);
        verify(cardRepository).deleteById(cardId);
    }

    @Test
    void delete_WhenCardNotExists_ShouldThrowException() {
        // Arrange
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> cardService.delete(cardId));
        verify(cardRepository).findById(cardId);
        verify(cardRepository, never()).deleteById(anyLong());
    }

    @Test
    void invalid_WhenCardIsActive_ShouldReturnFalse() {
        // Arrange
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(activeCard));

        // Act & Assert
        assertFalse(cardService.invalid(cardId));
    }

    @Test
    void invalid_WhenCardIsBlocked_ShouldReturnTrue() {
        // Arrange
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(blockedCard));

        // Act & Assert
        assertTrue(cardService.invalid(cardId));
    }
}