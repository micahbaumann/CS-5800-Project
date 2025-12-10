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
        Optional<MessageAccount> fromMessageAccount = messageAccountRepository.findMessageAccountByMessageAccountId((messageSendDto.getFrom()));
        Optional<MessageAccount> toMessageAccount = messageAccountRepository.findMessageAccountByMessageAccountId((messageSendDto.getTo()));

        if (fromMessageAccount.isPresent()) {
            if (toMessageAccount.isEmpty()) {
                throw new InvalidUserException(messageSendDto.getTo().toString());
            }

            boolean userAuthenticated = false;
            if (fromMessageAccount.get().getRole().equalsIgnoreCase("User") &&
                    fromMessageAccount.get().getUser().getUserId().equals(authContext.getUserId())) {
                userAuthenticated = true;
            } else if (fromMessageAccount.get().getRole().equalsIgnoreCase("Chef") &&
                    chefRepository.findById(fromMessageAccount.get().getChef().getChefId()).isPresent() &&
                    chefRepository.findById(fromMessageAccount.get().getChef().getChefId()).get().getUser().getUserId().equals(authContext.getUserId())
            ) {
                userAuthenticated = true;
            }

            if (userAuthenticated) {
                Message newMessage = new Message();
                newMessage.setFromAccount(fromMessageAccount.get());
                newMessage.setToAccount(toMessageAccount.get());
                newMessage.setContent(messageSendDto.getMessage());
                messageRepository.save(newMessage);
                return;
            }
        }

        throw new InvalidUserException(messageSendDto.getFrom().toString());
    }

    public Map<String, List<MessageReturnDto>> listUserMessages(AuthContext authContext) {
        try {
            MessageAccount messageAccount = createMessageAccountIfNotExist(authContext.getUserId(), "User");
            return getMessagesFromMessageAccount(messageAccount);
        } catch (UnsupportedOperationException e) {
            System.out.println(e);
            throw new InternalAppError("An error has occurred.");
        }
    }

    public Map<String, List<MessageReturnDto>> listChefMessages(AuthContext authContext, UUID chefId) {
        if (chefRepository.findById(chefId).isEmpty()) {
            throw new InvalidUserException(chefId.toString());
        }

        if (!chefRepository.findById(chefId).get().getUser().getUserId().equals(authContext.getUserId())) {
            throw new UnauthorizedUser(chefId.toString());
        }

        try {
            MessageAccount messageAccount = createMessageAccountIfNotExist(chefId, "Chef");
            return getMessagesFromMessageAccount(messageAccount);
        } catch (UnsupportedOperationException e) {
            System.out.println(e);
            throw new InternalAppError("An error has occurred.");
        }
    }

    public MessageReturnDto viewMessage(AuthContext authContext, UUID messageId) {
        if  (messageRepository.findMessageByMessageId(messageId).isEmpty()) {
            throw new InvalidUserException(messageId.toString());
        }

        Message foundMessage = messageRepository.findMessageByMessageId(messageId).get();

        // Figure out if foundMessage is associated with the user
        boolean userAuthenticated = false;
        if (foundMessage.getFromAccount().getRole().equalsIgnoreCase("User") &&
                foundMessage.getFromAccount().getUser().getUserId().equals(authContext.getUserId())
        ) {
            userAuthenticated = true;
        } else if (foundMessage.getFromAccount().getRole().equalsIgnoreCase("Chef") &&
                chefRepository.findById(foundMessage.getFromAccount().getChef().getChefId()).isPresent() &&
                foundMessage.getFromAccount().getChef().getUser().getUserId().equals(authContext.getUserId())
        ) {
            userAuthenticated = true;
        } else if (foundMessage.getToAccount().getRole().equalsIgnoreCase("User") &&
                foundMessage.getToAccount().getUser().getUserId().equals(authContext.getUserId())
        ) {
            userAuthenticated = true;
        } else if (foundMessage.getToAccount().getRole().equalsIgnoreCase("Chef") &&
                chefRepository.findById(foundMessage.getToAccount().getChef().getChefId()).isPresent() &&
                foundMessage.getToAccount().getChef().getUser().getUserId().equals(authContext.getUserId())
        ) {
            userAuthenticated = true;
        } else {
            throw  new InternalAppError("Internal message error.");
        }

        if (userAuthenticated) {
            return new MessageReturnDto(foundMessage.getMessageId(),
                    foundMessage.getFromAccount().getMessageAccountId(),
                    foundMessage.getToAccount().getMessageAccountId(),
                    foundMessage.getContent(),
                    foundMessage.getTimestamp());
        }

        throw  new UnauthorizedUser(authContext.getUserId().toString());
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
