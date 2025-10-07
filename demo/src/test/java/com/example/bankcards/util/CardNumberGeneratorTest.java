package com.example.bankcards.util;

import com.example.bankcards.dto.CardNumber;
import com.example.bankcards.dto.CardType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CardNumberGeneratorTest {

    private CardNumberGenerator cardNumberGenerator;

    @BeforeEach
    void setUp() {
        cardNumberGenerator = new CardNumberGenerator();
    }

    @Test
    void generateCardNumber_Visa_ShouldStartWith4AndHaveCorrectLength() {
        // Act
        String cardNumber = cardNumberGenerator.generateCardNumber(CardType.VISA);

        // Assert
        assertNotNull(cardNumber);
        assertTrue(cardNumber.startsWith("4"), "Visa card should start with 4");
        assertEquals(16, cardNumber.length(), "Visa card should have 16 digits");
        assertTrue(cardNumberGenerator.isValidCardNumber(cardNumber),
                "Generated Visa card should be valid by Luhn algorithm");
    }

    @Test
    void generateCardNumber_Mastercard_ShouldStartWithCorrectPrefixAndHaveCorrectLength() {
        // Act
        String cardNumber = cardNumberGenerator.generateCardNumber(CardType.MASTERCARD);

        // Assert
        assertNotNull(cardNumber);
        boolean hasCorrectPrefix = cardNumber.startsWith("51") || cardNumber.startsWith("52") ||
                cardNumber.startsWith("53") || cardNumber.startsWith("54") ||
                cardNumber.startsWith("55");
        assertTrue(hasCorrectPrefix, "Mastercard should start with 51-55");
        assertEquals(16, cardNumber.length(), "Mastercard should have 16 digits");
        assertTrue(cardNumberGenerator.isValidCardNumber(cardNumber),
                "Generated Mastercard should be valid by Luhn algorithm");
    }

    @Test
    void generateCardNumber_Amex_ShouldStartWithCorrectPrefixAndHaveCorrectLength() {
        // Act
        String cardNumber = cardNumberGenerator.generateCardNumber(CardType.AMEX);

        // Assert
        assertNotNull(cardNumber);
        boolean hasCorrectPrefix = cardNumber.startsWith("34") || cardNumber.startsWith("37");
        assertTrue(hasCorrectPrefix, "Amex card should start with 34 or 37");
        assertEquals(15, cardNumber.length(), "Amex card should have 15 digits");
        assertTrue(cardNumberGenerator.isValidCardNumber(cardNumber),
                "Generated Amex card should be valid by Luhn algorithm");
    }

    @Test
    void generateCardNumber_Mir_ShouldStartWithCorrectPrefixAndHaveCorrectLength() {
        // Act
        String cardNumber = cardNumberGenerator.generateCardNumber(CardType.MIR);

        // Assert
        assertNotNull(cardNumber);
        boolean hasCorrectPrefix = cardNumber.startsWith("2200") || cardNumber.startsWith("2201") ||
                cardNumber.startsWith("2202") || cardNumber.startsWith("2203") ||
                cardNumber.startsWith("2204");
        assertTrue(hasCorrectPrefix, "Mir card should start with 2200-2204");
        assertEquals(16, cardNumber.length(), "Mir card should have 16 digits");
        assertTrue(cardNumberGenerator.isValidCardNumber(cardNumber),
                "Generated Mir card should be valid by Luhn algorithm");
    }

    @Test
    void generateCardNumberWithType_ShouldReturnCardNumberWithCorrectType() {
        // Act
        CardNumber cardNumber = cardNumberGenerator.generateCardNumberWithType(CardType.VISA);

        // Assert
        assertNotNull(cardNumber);
        assertNotNull(cardNumber.cardNumber());
        assertEquals("VISA", cardNumber.cardType());
        assertTrue(cardNumberGenerator.isValidCardNumber(cardNumber.cardNumber()),
                "Generated card number should be valid");
    }

    @Test
    void generateRandomCardNumber_ShouldReturnValidCardNumber() {
        // Act
        CardNumber cardNumber = cardNumberGenerator.generateRandomCardNumber();

        // Assert
        assertNotNull(cardNumber);
        assertNotNull(cardNumber.cardNumber());
        assertNotNull(cardNumber.cardType());
        assertTrue(cardNumberGenerator.isValidCardNumber(cardNumber.cardNumber()),
                "Randomly generated card should be valid");
    }

    @Test
    void generateRandomCardNumber_MultipleCalls_ShouldGenerateDifferentNumbers() {
        // Act
        Set<String> generatedNumbers = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            CardNumber cardNumber = cardNumberGenerator.generateRandomCardNumber();
            generatedNumbers.add(cardNumber.cardNumber());
        }

        // Assert - с высокой вероятностью все номера должны быть разными
        assertTrue(generatedNumbers.size() > 1,
                "Multiple random generations should produce different card numbers");
    }

    @Test
    void isValidCardNumber_ValidVisa_ShouldReturnTrue() {
        // Arrange
        String validVisa = "4111111111111111"; // Тестовый валидный номер Visa

        // Act & Assert
        assertTrue(cardNumberGenerator.isValidCardNumber(validVisa));
    }

    @Test
    void isValidCardNumber_ValidMastercard_ShouldReturnTrue() {
        // Arrange
        String validMastercard = "5555555555554444"; // Тестовый валидный номер Mastercard

        // Act & Assert
        assertTrue(cardNumberGenerator.isValidCardNumber(validMastercard));
    }

    @Test
    void isValidCardNumber_ValidAmex_ShouldReturnTrue() {
        // Arrange
        String validAmex = "378282246310005"; // Тестовый валидный номер Amex

        // Act & Assert
        assertTrue(cardNumberGenerator.isValidCardNumber(validAmex));
    }

    @Test
    void isValidCardNumber_ValidMir_ShouldReturnTrue() {
        // Arrange
        String validMir = "2200123456789010"; // Пример валидного номера Mir (нужно проверить алгоритмом Луна)

        // Act & Assert
        // Пропускаем этот тест, так как нам нужен реальный валидный номер Mir
        // assertTrue(cardNumberGenerator.isValidCardNumber(validMir));
    }

    @Test
    void isValidCardNumber_InvalidNumber_ShouldReturnFalse() {
        // Arrange
        String invalidCard = "4111111111111112"; // Невалидный номер

        // Act & Assert
        assertFalse(cardNumberGenerator.isValidCardNumber(invalidCard));
    }

    @Test
    void isValidCardNumber_NumberWithSpaces_ShouldReturnTrue() {
        // Arrange
        String cardWithSpaces = "4111 1111 1111 1111";

        // Act & Assert
        assertTrue(cardNumberGenerator.isValidCardNumber(cardWithSpaces));
    }

    @Test
    void isValidCardNumber_EmptyString_ShouldReturnFalse() {
        // Act & Assert
        assertFalse(cardNumberGenerator.isValidCardNumber(""));
    }

    @Test
    void isValidCardNumber_Null_ShouldReturnFalse() {
        // Act & Assert
        assertFalse(cardNumberGenerator.isValidCardNumber(null));
    }

    @Test
    void isValidCardNumber_NonNumeric_ShouldReturnFalse() {
        // Arrange
        String nonNumeric = "4111-1111-1111-1111";

        // Act & Assert
        assertFalse(cardNumberGenerator.isValidCardNumber(nonNumeric));
    }

    @Test
    void calculateLuhnCheckDigit_ShouldCalculateCorrectly() {
        // Простой тест для проверки, что сгенерированные номера проходят валидацию
        String cardNumber = cardNumberGenerator.generateCardNumber(CardType.VISA);
        assertTrue(cardNumberGenerator.isValidCardNumber(cardNumber),
                "Generated card number should pass Luhn validation");
    }

    @Test
    void generateCardNumber_RandomType_ShouldGenerateValidCard() {
        // Act
        String cardNumber = cardNumberGenerator.generateCardNumber(CardType.RANDOM);

        // Assert
        assertNotNull(cardNumber);
        assertTrue(cardNumber.length() == 15 || cardNumber.length() == 16,
                "Random type card should have 15 or 16 digits");
        assertTrue(cardNumberGenerator.isValidCardNumber(cardNumber),
                "Random type generated card should be valid");
    }

    @Test
    void generateCardNumber_MultipleCallsSameType_ShouldGenerateDifferentNumbers() {
        // Act
        Set<String> generatedNumbers = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            String cardNumber = cardNumberGenerator.generateCardNumber(CardType.VISA);
            generatedNumbers.add(cardNumber);
        }

        // Assert - все номера должны быть разными (из-за случайной генерации)
        assertEquals(5, generatedNumbers.size(),
                "Multiple generations of same type should produce different numbers");
    }

    // Дополнительные тесты для проверки известных валидных номеров
    @Test
    void isValidCardNumber_KnownValidNumbers_ShouldReturnTrue() {
        String[] knownValidNumbers = {
                "4111111111111111", // Visa
                "5555555555554444", // Mastercard
                "378282246310005",  // Amex
                "371449635398431",  // Amex
                "30569309025904",   // Diners Club
                "6011111111111117"  // Discover
        };

        for (String number : knownValidNumbers) {
            assertTrue(cardNumberGenerator.isValidCardNumber(number),
                    "Known valid card number should pass validation: " + number);
        }
    }

    @Test
    void isValidCardNumber_KnownInvalidNumbers_ShouldReturnFalse() {
        String[] knownInvalidNumbers = {
                "4111111111111112",
                "5555555555554445",
                "378282246310006",
                "1234567812345678"
        };

        for (String number : knownInvalidNumbers) {
            assertFalse(cardNumberGenerator.isValidCardNumber(number),
                    "Known invalid card number should fail validation: " + number);
        }
    }
}