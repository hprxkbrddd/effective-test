package com.example.bankcards.util;

import jakarta.persistence.AttributeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

@Component
public class CardNumberEncryptor implements AttributeConverter<String, String> {

    @Autowired
    @Qualifier("textEncryptor")
    private TextEncryptor encryptor;

    @Override
    public String convertToDatabaseColumn(String s) {
        return encryptor.encrypt(s);
    }

    @Override
    public String convertToEntityAttribute(String s) {
        return encryptor.decrypt(s);
    }
}
