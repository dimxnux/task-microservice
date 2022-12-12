package com.internship.microservice.model;

import org.springframework.security.crypto.encrypt.BytesEncryptor;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;

@Entity
@Table(name = "databases")
public class Database {
    @Id
    private String name;

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

    public Database(String name, String url, String username, String driverClassName, byte[] encryptedPassword) {
        this.name = name;
        this.url = url;
        this.username = username;
        this.driverClassName = driverClassName;
        this.encryptedPassword = encryptedPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

        return name.equals(database.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Database{name='%s', url='%s', username='%s', driverClassName='%s'}",
                name, url, username, driverClassName);
    }
}
