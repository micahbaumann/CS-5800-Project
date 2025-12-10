package com.chachef.controller;

import com.chachef.annotations.RequireAuth;
import com.chachef.dataobjects.AuthContext;
import com.chachef.dto.MessageReturnDto;
import com.chachef.dto.MessageSendDto;
import com.chachef.entity.MessageAccount;
import com.chachef.service.MessageService;
import com.chachef.service.exceptions.InternalAppError;
import com.chachef.service.exceptions.InvalidUserException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @RequireAuth
    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestAttribute(value = "auth") AuthContext authContext, @Valid @RequestBody MessageSendDto messageSendDto) {
        messageService.sendMessage(authContext, messageSendDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequireAuth
    @GetMapping("/list/user")
    public ResponseEntity<Map<String, List<MessageReturnDto>>> listUserMessages(@RequestAttribute(value = "auth") AuthContext authContext) {

        return new ResponseEntity<>(messageService.listUserMessages(authContext), HttpStatus.OK);
    }

    @RequireAuth
    @GetMapping("/list/chef/{chefId}")
    public ResponseEntity<Map<String, List<MessageReturnDto>>> listChefMessages(@RequestAttribute(value = "auth") AuthContext authContext,  @PathVariable UUID chefId) {

        return new ResponseEntity<>(messageService.listChefMessages(authContext, chefId), HttpStatus.OK);
    }

    @RequireAuth
    @GetMapping("/view/{messageId}")
    public ResponseEntity<MessageReturnDto> viewMessage(@RequestAttribute(value = "auth") AuthContext authContext,  @PathVariable UUID messageId) {

        return new ResponseEntity<>(messageService.viewMessage(authContext, messageId), HttpStatus.OK);
    }

    @RequireAuth
    @GetMapping("/user/message-account")
    public ResponseEntity<Map<String, String>> userMessageAccount(@RequestAttribute(value = "auth") AuthContext authContext) {
        try {
            return new ResponseEntity<>(Map.of("message_account_id", messageService.getCreateMessageAccount(authContext).getMessageAccountId().toString()), HttpStatus.OK);
        } catch (InvalidUserException e) {
            throw new InvalidUserException(e.getMessage());
        } catch (Exception e) {
            throw new InternalAppError("An error has occurred.");
        }
    }

    @RequireAuth
    @GetMapping("/chef/{chefId}/message-account")
    public ResponseEntity<Map<String, String>> chefMessageAccount(@RequestAttribute(value = "auth") AuthContext authContext,  @PathVariable UUID chefId) {
        try {
            return new ResponseEntity<>(Map.of("message_account_id", messageService.getCreateMessageAccount(authContext, chefId).getMessageAccountId().toString()), HttpStatus.OK);
        } catch (InvalidUserException e) {
            throw new InvalidUserException(e.getMessage());
        } catch (Exception e) {
            System.out.println(e);
            throw new InternalAppError("An error has occurred.");
        }
    }
}
