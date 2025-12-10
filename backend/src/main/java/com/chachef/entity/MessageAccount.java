package com.chachef.entity;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
public class MessageAccount {
    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.CHAR)   // store as CHAR
    @Column(name = "message_account_id", length = 36, updatable = false, nullable = false)
    private UUID messageAccountId;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "user", nullable = true)
    @JsonIdentityReference(alwaysAsId = true)
    private User user = null;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "chef", nullable = true)
    @JsonIdentityReference(alwaysAsId = true)
    private Chef chef = null;

    @Column(name = "role", nullable = false)
    public String role;

    public MessageAccount() {}

    public MessageAccount(Chef chef) {
        this.chef = chef;
        this.role = "Chef";
    }

    public MessageAccount(User user) {
        this.user = user;
        this.role = "User";
    }

    public UUID getMessageAccountId() {
        return messageAccountId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Chef getChef() {
        return chef;
    }

    public void setChef(Chef chef) {
        this.chef = chef;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
