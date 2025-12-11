package com.chachef.entity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MessageAccountTest {

    @Test
    void defaultConstructor_hasPendingStatus_andNullsElsewhere() {
        MessageAccount messageAccount = new MessageAccount();

        assertNull(messageAccount.getMessageAccountId());
        assertNull(messageAccount.getUser());
        assertNull(messageAccount.getChef());
        assertNull(messageAccount.getRole());
    }

    @Test
    void allArgsConstructor_setsAllFields() {
        User user = new User();
        Chef chef = new Chef();

        MessageAccount messageAccountUser = new MessageAccount(user);
        MessageAccount messageAccountChef = new MessageAccount(chef);

        assertSame(user, messageAccountUser.getUser());
        assertSame("User", messageAccountUser.getRole());
        assertSame(chef, messageAccountChef.getChef());
        assertSame("Chef", messageAccountChef.getRole());
    }

    @Test
    void setMessageAccountId() {
        User user = new User();
        UUID id = UUID.randomUUID();

        MessageAccount messageAccountUser = new MessageAccount(user);
        messageAccountUser.setMessageAccountId(id);

        assertSame(id, messageAccountUser.getMessageAccountId());
    }

    @Test
    void getMessageAccountId() {
        User user = new User();
        Chef chef = new Chef();

        MessageAccount messageAccountUser = new MessageAccount(user);
        MessageAccount messageAccountChef = new MessageAccount(chef);

        assertNull(messageAccountUser.getMessageAccountId());
        assertNull(messageAccountChef.getMessageAccountId());
    }

    @Test
    void getUser() {
        User user = new User();

        MessageAccount messageAccountUser = new MessageAccount(user);

        assertSame(user, messageAccountUser.getUser());
    }

    @Test
    void setUser() {
        User user = new User();
        User newUser = new User();

        MessageAccount messageAccountUser = new MessageAccount(user);
        messageAccountUser.setUser(newUser);

        assertSame(newUser, messageAccountUser.getUser());
    }

    @Test
    void getChef() {
        Chef chef = new Chef();

        MessageAccount messageAccountChef = new MessageAccount(chef);

        assertSame(chef, messageAccountChef.getChef());
    }

    @Test
    void setChef() {
        Chef chef = new Chef();
        Chef newChef = new Chef();

        MessageAccount messageAccountChef = new MessageAccount(chef);
        messageAccountChef.setChef(newChef);

        assertSame(newChef, messageAccountChef.getChef());
    }

    @Test
    void getRole() {
        User user = new User();
        Chef chef = new Chef();

        MessageAccount messageAccountUser = new MessageAccount(user);
        MessageAccount messageAccountChef = new MessageAccount(chef);

        assertSame("User", messageAccountUser.getRole());
        assertSame("Chef", messageAccountChef.getRole());
    }

    @Test
    void setRole() {
        User user = new User();

        MessageAccount messageAccountUser = new MessageAccount(user);
        messageAccountUser.setRole("Chef");

        assertSame("Chef", messageAccountUser.getRole());
    }
}