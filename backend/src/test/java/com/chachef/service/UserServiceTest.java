package com.chachef.service;

import com.chachef.dto.UserCreateDto;
import com.chachef.entity.User;
import com.chachef.repository.UserRepository;
import com.chachef.service.exceptions.UsernameTakenException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser() {
        UserCreateDto dto = new UserCreateDto("test", "Test Name");

        when(userRepository.existsByUsername("test")).thenReturn(false);
        when(userRepository.save(any(User.class)))
                .thenAnswer(inv -> inv.getArgument(0)); // echo back the entity

        User saved = userService.createUser(dto);

        assertNotNull(saved);
        assertEquals("test", saved.getUsername());
        assertEquals("Test Name", saved.getName());

        verify(userRepository).existsByUsername("test");
        verify(userRepository).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void createUser_usernameTaken() {
        UserCreateDto dto = new UserCreateDto("taken_user", "Someone");

        when(userRepository.existsByUsername("taken_user")).thenReturn(true);

        assertThrows(UsernameTakenException.class, () -> userService.createUser(dto));

        verify(userRepository).existsByUsername("taken_user");
        verify(userRepository, never()).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAllUsers() {
        var u1 = new User("a", "A");
        var u2 = new User("b", "B");
        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertSame(u1, result.get(0));
        assertSame(u2, result.get(1));

        verify(userRepository).findAll();
        verifyNoMoreInteractions(userRepository);
    }
}