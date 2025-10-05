package com.example.bankcards.util;

import com.example.bankcards.dto.CardNumber;
import com.example.bankcards.dto.CardType;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class CardNumberGenerator {

    private final Random random = new Random();

    // Префиксы для разных типов карт
    private static final String[] VISA_PREFIXES = {"4"};
    private static final String[] MASTERCARD_PREFIXES = {"51", "52", "53", "54", "55"};
    private static final String[] AMEX_PREFIXES = {"34", "37"};
    private static final String[] MIR_PREFIXES = {"2200", "2201", "2202", "2203", "2204"};

    /**
     * Генерирует номер карты указанного типа
     */
    public String generateCardNumber(CardType cardType) {
        String prefix = getPrefix(cardType);
        int length = getCardLength(cardType);

        // Генерируем номер без контрольной цифры
        StringBuilder cardNumber = new StringBuilder(prefix);
        while (cardNumber.length() < length - 1) {
            cardNumber.append(random.nextInt(10));
        }

        // Добавляем контрольную цифру (алгоритм Луна)
        String cardNumberWithoutCheckDigit = cardNumber.toString();
        int checkDigit = calculateLuhnCheckDigit(cardNumberWithoutCheckDigit);
        cardNumber.append(checkDigit);

        return cardNumber.toString();
    }

    /**
     * Генерирует номер карты с указанием типа
     */
    public CardNumber generateCardNumberWithType(CardType cardType) {
        String cardNumber = generateCardNumber(cardType);
        return new CardNumber(cardNumber, cardType.name());
    }

    /**
     * Генерирует номер карты случайного типа
     */
    public CardNumber generateRandomCardNumber() {
        CardType[] types = {CardType.VISA, CardType.MASTERCARD, CardType.AMEX, CardType.MIR};
        CardType randomType = types[random.nextInt(types.length)];
        return generateCardNumberWithType(randomType);
    }

    private String getPrefix(CardType cardType) {
        return switch (cardType) {
            case VISA -> VISA_PREFIXES[random.nextInt(VISA_PREFIXES.length)];
            case MASTERCARD -> MASTERCARD_PREFIXES[random.nextInt(MASTERCARD_PREFIXES.length)];
            case AMEX -> AMEX_PREFIXES[random.nextInt(AMEX_PREFIXES.length)];
            case MIR -> MIR_PREFIXES[random.nextInt(MIR_PREFIXES.length)];
            case RANDOM -> {
                CardType[] types = {CardType.VISA, CardType.MASTERCARD, CardType.AMEX, CardType.MIR};
                yield getPrefix(types[random.nextInt(types.length)]);
            }
        };
    }

    private int getCardLength(CardType cardType) {
        return switch (cardType) {
            case AMEX -> 15;
            default -> 16;
        };
    }

    /**
     * Реализация алгоритма Луна для расчета контрольной цифры
     */
    private int calculateLuhnCheckDigit(String number) {
        int sum = 0;
        boolean alternate = false;

        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(number.charAt(i));

            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }

            sum += digit;
            alternate = !alternate;
        }

        int checkDigit = (10 - (sum % 10)) % 10;
        return checkDigit;
    }

    /**
     * Проверяет валидность номера карты по алгоритму Луна
     */
    public boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            return false;
        }

        String cleanNumber = cardNumber.replaceAll("\\s", "");
        if (!cleanNumber.matches("\\d+")) {
            return false;
        }

        int sum = 0;
        boolean alternate = false;

        for (int i = cleanNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cleanNumber.charAt(i));

            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }

            sum += digit;
            alternate = !alternate;
        }

        return (sum % 10) == 0;
    }
}
