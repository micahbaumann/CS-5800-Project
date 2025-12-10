package com.chachef.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @Test
    void defaultConstructor_hasPendingStatus_andNullsElsewhere() {
        Message message = new Message();

        assertNull(message.getMessageId());
        assertNull(message.getFromAccount());
        assertNull(message.getToAccount());
        assertNull(message.getContent());
        assertNull(message.getTimestamp());
    }

    @Test
    void allArgsConstructor_setsAllFields() {
        MessageAccount from = new MessageAccount();
        MessageAccount to = new MessageAccount();
        String content = "content";

        Message message = new Message(from, to, content);

        assertSame(from, message.getFromAccount());
        assertSame(to, message.getToAccount());
        assertEquals(content, message.getContent());
    }

    @Test
    void getMessageId() {
        MessageAccount from = new MessageAccount();
        MessageAccount to = new MessageAccount();
        String content = "content";

        Message message = new Message(from, to, content);

        assertNull(message.getMessageId());
    }

    @Test
    void getFromAccount() {
        MessageAccount from = new MessageAccount();
        MessageAccount to = new MessageAccount();
        String content = "content";

        Message message = new Message(from, to, content);
        assertSame(from, message.getFromAccount());
    }

    @Test
    void setFromAccount() {
        MessageAccount from = new MessageAccount();
        MessageAccount from2 = new MessageAccount();
        MessageAccount to = new MessageAccount();
        String content = "content";

        Message message = new Message(from, to, content);
        message.setFromAccount(from2);
        assertSame(from2, message.getFromAccount());
    }

    @Test
    void getToAccount() {
        MessageAccount from = new MessageAccount();
        MessageAccount to = new MessageAccount();
        String content = "content";

        Message message = new Message(from, to, content);
        assertSame(to, message.getToAccount());
    }

    @Test
    void setToAccount() {
        MessageAccount from = new MessageAccount();
        MessageAccount to = new MessageAccount();
        MessageAccount to2 = new MessageAccount();
        String content = "content";

        Message message = new Message(from, to, content);
        message.setToAccount(to2);
        assertSame(to2, message.getToAccount());
    }

    @Test
    void getContent() {
        MessageAccount from = new MessageAccount();
        MessageAccount to = new MessageAccount();
        String content = "content";

        Message message = new Message(from, to, content);
        assertSame(content, message.getContent());
    }

    @Test
    void setContent() {
        MessageAccount from = new MessageAccount();
        MessageAccount to = new MessageAccount();
        String content = "content";
        String content2 = "content2";

        Message message = new Message(from, to, content);
        message.setContent(content2);
        assertSame(content2, message.getContent());
    }

    @Test
    void getTimestamp() {
        MessageAccount from = new MessageAccount();
        MessageAccount to = new MessageAccount();
        String content = "content";

        Message message = new Message(from, to, content);

        assertNull(message.getTimestamp());
    }
}