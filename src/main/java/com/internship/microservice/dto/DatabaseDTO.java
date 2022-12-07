package com.internship.microservice.dto;

import com.internship.microservice.model.Database;
import org.springframework.security.crypto.encrypt.BytesEncryptor;

import javax.validation.constraints.NotBlank;

public class DatabaseDTO {
    @NotBlank
    private String alias;
    @NotBlank
    private String url;
    @NotBlank
    private String username;
    @NotBlank
    private String driverClassName;
    @NotBlank
    private String password;

    public DatabaseDTO() {
    }

    public DatabaseDTO(String alias, String url, String username, String driverClassName, String password) {
        this.alias = alias;
        this.url = url;
        this.username = username;
        this.driverClassName = driverClassName;
        this.password = password;
    }

    public String getAlias() {
        return alias;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getPassword() {
        return password;
    }

    /**
     * @param bytesEncryptor the encryptor used for encrypting the database password
     * @return the {@link Database} representation of this DTO
     */
    public Database toDatabase(BytesEncryptor bytesEncryptor) {
        byte[] encryptedPassword = bytesEncryptor.encrypt(password.getBytes());

        return new Database(alias, url, username, driverClassName, encryptedPassword);
    }
}
