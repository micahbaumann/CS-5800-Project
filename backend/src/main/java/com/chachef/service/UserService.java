package com.chachef.service;

import com.chachef.entity.Chef;
import com.chachef.entity.User;
import com.chachef.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void createUser() {
        final User myUser = new User();
        myUser.setUsername("micah");
        myUser.setName("Micah");

        userRepository.save(myUser);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
