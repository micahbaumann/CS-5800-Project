package com.chachef.service;

import com.chachef.dataobjects.AuthContext;
import com.chachef.dto.MessageSendDto;
import com.chachef.entity.Chef;
import com.chachef.entity.Message;
import com.chachef.entity.MessageAccount;
import com.chachef.entity.User;
import com.chachef.repository.ChefRepository;
import com.chachef.repository.MessageAccountRepository;
import com.chachef.repository.MessageRepository;
import com.chachef.repository.UserRepository;
import com.chachef.service.exceptions.InvalidUserException;
import com.chachef.service.exceptions.UnauthorizedUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageAccountRepository messageAccountRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChefRepository chefRepository;

    @InjectMocks
    private MessageService messageService;

    @Test
    void sendMessage_fromUser_success() {
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        AuthContext authContext = new AuthContext(userId, "user", "Test User");
        MessageSendDto dto = new MessageSendDto(fromId, toId, "Hello");

        User fromUser = new User();
        fromUser.setUserId(userId);

        MessageAccount fromAccount = new MessageAccount();
        fromAccount.setRole("User");
        fromAccount.setUser(fromUser);
        fromAccount.setMessageAccountId(fromId);

        MessageAccount toAccount = new MessageAccount();
        toAccount.setMessageAccountId(toId);


        when(messageAccountRepository.findMessageAccountByMessageAccountId(fromId)).thenReturn(Optional.of(fromAccount));
        when(messageAccountRepository.findMessageAccountByMessageAccountId(toId)).thenReturn(Optional.of(toAccount));

        messageService.sendMessage(authContext, dto);

        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void sendMessage_unauthorized() {
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID wrongUserId = UUID.randomUUID();

        AuthContext authContext = new AuthContext(wrongUserId, "user", "Test User");
        MessageSendDto dto = new MessageSendDto(fromId, toId, "Hello");

        User fromUser = new User();
        fromUser.setUserId(userId);

        MessageAccount fromAccount = new MessageAccount();
        fromAccount.setRole("User");
        fromAccount.setUser(fromUser);

        when(messageAccountRepository.findMessageAccountByMessageAccountId(fromId)).thenReturn(Optional.of(fromAccount));
        when(messageAccountRepository.findMessageAccountByMessageAccountId(toId)).thenReturn(Optional.of(new MessageAccount()));

        // This should not throw an exception handled by the method but the framework will handle it
        assertThrows(InvalidUserException.class, () -> messageService.sendMessage(authContext, dto));
    }


    @Test
    void listUserMessages() {
        UUID userId = UUID.randomUUID();
        AuthContext authContext = new AuthContext(userId, "user", "name");
        User user = new User();
        user.setUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(messageAccountRepository.findByUser_UserId(userId)).thenReturn(Optional.empty());
        when(messageAccountRepository.save(any(MessageAccount.class))).thenAnswer(inv -> {
            MessageAccount ma = inv.getArgument(0);
            ma.setMessageAccountId(UUID.randomUUID()); // Assign a UUID for the new account
            return ma;
        });
        when(messageRepository.findByFromAccount_MessageAccountId(any(UUID.class))).thenReturn(Optional.of(new java.util.ArrayList<>()));
        when(messageRepository.findByToAccount_MessageAccountId(any(UUID.class))).thenReturn(Optional.of(new java.util.ArrayList<>()));

        messageService.listUserMessages(authContext);

        verify(messageAccountRepository).save(any(MessageAccount.class));
    }

    @Test
    void listChefMessages_unauthorized() {
        UUID chefId = UUID.randomUUID();
        UUID wrongUserId = UUID.randomUUID();

        AuthContext authContext = new AuthContext(wrongUserId, "user", "name");
        Chef chef = new Chef();
        User user = new User();
        user.setUserId(UUID.randomUUID());
        chef.setUser(user);

        when(chefRepository.findById(chefId)).thenReturn(Optional.of(chef));

        assertThrows(UnauthorizedUser.class, () -> messageService.listChefMessages(authContext, chefId));
    }

    @Test
    void viewMessage_unauthorized() {
        UUID messageId = UUID.randomUUID();
        UUID wrongUserId = UUID.randomUUID();
        AuthContext authContext = new AuthContext(wrongUserId, "user", "name");
        Message message = new Message();
        MessageAccount fromAccount = new MessageAccount();
        MessageAccount toAccount = new MessageAccount();

        User user = new User();
        user.setUserId(UUID.randomUUID());

        fromAccount.setRole("User");
        fromAccount.setUser(user);

        toAccount.setRole("User");
        toAccount.setUser(user);

        message.setFromAccount(fromAccount);
        message.setToAccount(toAccount);

        when(messageRepository.findMessageByMessageId(messageId)).thenReturn(Optional.of(message));

        assertThrows(UnauthorizedUser.class, () -> messageService.viewMessage(authContext, messageId));
    }

    @Test
    void getCreateMessageAccount_forUser() {
        UUID userId = UUID.randomUUID();
        AuthContext authContext = new AuthContext(userId, "user", "name");
        User user = new User();
        user.setUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(messageAccountRepository.findByUser_UserId(userId)).thenReturn(Optional.empty());

        messageService.getCreateMessageAccount(authContext);

        verify(messageAccountRepository).save(any(MessageAccount.class));
    }

    @Test
    void getCreateMessageAccount_forChef_unauthorized() {
        UUID chefId = UUID.randomUUID();
        UUID wrongUserId = UUID.randomUUID();

        AuthContext authContext = new AuthContext(wrongUserId, "user", "name");
        Chef chef = new Chef();
        User user = new User();
        user.setUserId(UUID.randomUUID());
        chef.setUser(user);

        when(chefRepository.findByChefId(chefId)).thenReturn(Optional.of(chef));

        assertThrows(UnauthorizedUser.class, () -> messageService.getCreateMessageAccount(authContext, chefId));
    }
}