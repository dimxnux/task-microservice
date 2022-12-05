package com.internship.microservice.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;

@Configuration
public class PasswordEncryptorConfig {
    @Value("${app.security.encryptor.password}")
    private String encoderPassword;

    @Value("${app.security.encryptor.salt}")
    private String encoderSalt;

    @Bean
    public BytesEncryptor dataSourcePasswordEncryptor() {
        return Encryptors.stronger(encoderPassword, encoderSalt);
    }
}
