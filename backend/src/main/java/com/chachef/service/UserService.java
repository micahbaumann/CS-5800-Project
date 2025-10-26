package com.chachef.service;

import com.chachef.dto.UserCreateDto;
import com.chachef.entity.User;
import com.chachef.repository.UserRepository;
import com.chachef.service.exceptions.UsernameTakenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(UserCreateDto userCreateDto) {
        if (userRepository.existsByUsername(userCreateDto.getUsername())) {
            throw new UsernameTakenException(userCreateDto.getUsername());
        }

        final User myUser = new User();
        myUser.setUsername(userCreateDto.getUsername());
        myUser.setName(userCreateDto.getName());

        return userRepository.save(myUser);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
