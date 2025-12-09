package com.chachef.controller;

import com.chachef.annotations.RequireAuth;
import com.chachef.dataobjects.AuthContext;
import com.chachef.dto.UserCreateDto;
import com.chachef.dto.UserReturnDto;
import com.chachef.entity.User;
import com.chachef.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<UserReturnDto> addUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        User returnedUser = userService.createUser(userCreateDto);
        return new ResponseEntity<>(new UserReturnDto(returnedUser.getUserId(), returnedUser.getUsername(), returnedUser.getName()), HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserReturnDto>> getAllUsers() {
        List<User> returnedUser = userService.getAllUsers();
        List<UserReturnDto> userReturnDtos = new ArrayList<>();
        for(User user : returnedUser) {
            userReturnDtos.add(new UserReturnDto(user.getUserId(), user.getUsername(), user.getName()));
        }
        return new ResponseEntity<>(userReturnDtos, HttpStatus.OK);
    }

    @RequireAuth
    @GetMapping("/view")
    public ResponseEntity<UserReturnDto> getUser(@RequestAttribute(value = "auth") AuthContext authContext) {
        User returnedUser = userService.getUser(authContext.getUserId());
        return new ResponseEntity<>(new UserReturnDto(returnedUser.getUserId(), returnedUser.getUsername(), returnedUser.getName()), HttpStatus.OK);
    }
}
