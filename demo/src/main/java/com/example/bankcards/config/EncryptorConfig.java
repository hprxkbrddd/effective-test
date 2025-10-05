package com.example.bankcards.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

@Configuration
public class EncryptorConfig {

    @Value("${app.encryptor.salt}")
    private String salt;
    @Value("${app.encryptor.password}")
    private String password;

    @Bean(name = "textEncryptor")
    public TextEncryptor getEncryptor() {
        return Encryptors.text(password, salt);
    }
}
