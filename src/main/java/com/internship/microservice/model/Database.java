package com.internship.microservice.model;

import org.springframework.security.crypto.encrypt.BytesEncryptor;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Entity
@Table(name = "databases")
public class Database {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String alias;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String driverClassName;

    @Basic
    @Column(nullable = false)
    private byte[] encryptedPassword;

    public Database() {
    }

    public Database(String alias, String url, String username, String driverClassName, byte[] encryptedPassword) {
        this.alias = alias;
        this.url = url;
        this.username = username;
        this.driverClassName = driverClassName;
        this.encryptedPassword = encryptedPassword;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String name) {
        this.alias = name;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(byte[] encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String decryptPassword(BytesEncryptor encryptor) {
        byte[] decryptedBytes = encryptor.decrypt(encryptedPassword);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Database database = (Database) o;
        return Objects.equals(id, database.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Database{id=%d, alias='%s', url='%s', username='%s', driverClassName='%s'}",
                id, alias, url, username, driverClassName);
    }
}
