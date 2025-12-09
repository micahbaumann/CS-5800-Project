package com.chachef.entity;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class RefreshTokens {
    @Id
    @JdbcTypeCode(SqlTypes.CHAR)   // store as CHAR
    @Column(name = "refresh_id", length = 36, updatable = false, nullable = false)
    private UUID refreshId;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIdentityReference(alwaysAsId = true)
    private User user;

    @Column(name = "`expires`", nullable = false)
    public LocalDateTime expires;

    public RefreshTokens() {}

    public RefreshTokens(UUID refreshId, LocalDateTime expires, User user) {
        this.refreshId = refreshId;
        this.expires = expires;
        this.user = user;
    }

    public UUID getRefreshId() {
        return refreshId;
    }

    public LocalDateTime getExpires() {
        return expires;
    }

    public void setExpires(LocalDateTime expires) {
        this.expires = expires;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
