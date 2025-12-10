package com.chachef.entity;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Message {
    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.CHAR)   // store as CHAR
    @Column(name = "message_id", length = 36, updatable = false, nullable = false)
    private UUID messageId;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "from_account", nullable = false)
    @JsonIdentityReference(alwaysAsId = true)
    private MessageAccount fromAccount;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "to_account", nullable = false)
    @JsonIdentityReference(alwaysAsId = true)
    private MessageAccount toAccount;

    @Column(name = "content", nullable = false)
    public String content;

    @Column(name = "timestamp", nullable = false)
    public LocalDateTime timestamp;

    public Message() {}

    public Message(MessageAccount fromAccount, MessageAccount toAccount, String content) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.content = content;
    }

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

    public UUID getMessageId() {
        return messageId;
    }

    public MessageAccount getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(MessageAccount fromAccount) {
        this.fromAccount = fromAccount;
    }

    public MessageAccount getToAccount() {
        return toAccount;
    }

    public void setToAccount(MessageAccount toAccount) {
        this.toAccount = toAccount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
