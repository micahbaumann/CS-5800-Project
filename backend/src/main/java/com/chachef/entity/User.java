package com.chachef.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;


import java.util.UUID;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
            @UniqueConstraint(columnNames = "username")
    }
)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "userId"
)
public class User {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.CHAR)   // store as CHAR
    @Column(name = "user_id", length = 36, updatable = false, nullable = false)
    private UUID userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String name;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // Constructors
    public User() {}

    public User(String username, String name, String passwordHash) {
        this.username = username;
        this.name = name;
        this.passwordHash = passwordHash;
    }

    // Getters and setters
    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
