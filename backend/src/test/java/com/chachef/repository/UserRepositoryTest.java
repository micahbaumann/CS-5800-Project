package com.chachef.repository;

import com.chachef.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    private final String USERNAME = "testuser";

    private User saveAndPersistSampleUser() {
        User user = new User(USERNAME, "Johny Appleseed");

        return userRepository.save(user);
    }

    @Test
    public void testSaveUser() {
        User savedUser = saveAndPersistSampleUser();

        assertNotNull(savedUser);
    }

    @Test
    public void testFindById() {
        User savedUser = saveAndPersistSampleUser();

        User retrievedUser = userRepository.findByUserId(savedUser.getUserId()).get();

        assertEquals(savedUser, retrievedUser);
    }

    @Test
    public void existsByUserId() {
        User savedUser = saveAndPersistSampleUser();

        assertTrue(userRepository.existsById(savedUser.getUserId()));
    }

    @Test
    public void findByUsername() {
        User savedUser = saveAndPersistSampleUser();

        User retrievedUser = userRepository.findByUsername(savedUser.getUsername()).get();

        assertEquals(savedUser, retrievedUser);
    }
}