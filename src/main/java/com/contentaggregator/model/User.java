package com.contentaggregator.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"user\"")
public class User {

    public User(){};

    public User(String username, String email, String cognitoUuid, int userId) {
        this.username = username;
        this.cognitoUuid = cognitoUuid;
        this.email = email;
    }

    public User(String username, String email, String cognitoUuid) {
        this.username = username;
        this.email = email;
        this.cognitoUuid = cognitoUuid;
        this.createdAt = LocalDateTime.now();
        this.lastLogin = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime lastLogin;

    @Column(nullable = false, unique = true)
    private String cognitoUuid;


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCognitoUuid() {
        return cognitoUuid;
    }

    public void setCognitoUuid(String cognitoUuid) {
        this.cognitoUuid = cognitoUuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
}
