package com.chachef.controller;

import com.chachef.dto.UserCreateDto;
import com.chachef.entity.User;
import com.chachef.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<Void> addUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        userService.createUser(userCreateDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> getUser() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }
}
