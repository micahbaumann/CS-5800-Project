package com.chachef.service;

import com.chachef.dataobjects.AuthContext;
import com.chachef.dto.MessageReturnDto;
import com.chachef.dto.MessageSendDto;
import com.chachef.entity.Chef;
import com.chachef.entity.Message;
import com.chachef.entity.MessageAccount;
import com.chachef.entity.User;
import com.chachef.repository.ChefRepository;
import com.chachef.repository.MessageAccountRepository;
import com.chachef.repository.MessageRepository;
import com.chachef.repository.UserRepository;
import com.chachef.service.exceptions.InternalAppError;
import com.chachef.service.exceptions.InvalidUserException;
import com.chachef.service.exceptions.UnauthorizedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MessageService {

    @Autowired
    private MessageAccountRepository messageAccountRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChefRepository chefRepository;

    public void sendMessage(AuthContext authContext, MessageSendDto messageSendDto) {

    }

    public Map<String, List<MessageReturnDto>> listUserMessages(AuthContext authContext) {
        return null;
    }

    public Map<String, List<MessageReturnDto>> listChefMessages(AuthContext authContext, UUID chefId) {
        return null;
    }

    public MessageReturnDto viewMessage(AuthContext authContext, UUID messageId) {
        return null;
    }

    public MessageAccount getCreateMessageAccount(AuthContext authContext, UUID chefId) {
        return null;
    }

    public MessageAccount getCreateMessageAccount(AuthContext authContext) {
        return null;
    }

    private MessageAccount createMessageAccountIfNotExist(UUID userChefID, String type) {
        return null;
    }

    private Map<String, List<MessageReturnDto>> getMessagesFromMessageAccount(MessageAccount messageAccount) {
        return null;
    }
}
