package com.chachef.controller;


import com.chachef.entity.User;
import com.chachef.service.UserService;
import com.chachef.service.exceptions.InvalidUserException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void addUser() throws Exception {
        User saved = new User("jdoe", "John Doe");

        when(userService.createUser(any())).thenReturn(saved);

        mockMvc.perform(post("/user/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                             { "username": "jdoe", "name": "John Doe" }
                             """))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.username", is("jdoe")))
            .andExpect(jsonPath("$.name", is("John Doe")));

        verify(userService, times(1)).createUser(any());
    }

    @Test
    void addUser_400Error() throws Exception {
        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void getAllUsers() throws Exception {
        User u1 = new User("alice", "Alice A.");
        User u2 = new User("bob", "Bob B.");
        when(userService.getAllUsers()).thenReturn(List.of(u1, u2));

        mockMvc.perform(get("/user/list"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username", is("alice")))
                .andExpect(jsonPath("$[1].username", is("bob")));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUser() throws Exception {
        UUID id = UUID.randomUUID();
        User u1 = new User("alice", "Alice A.");

        when(userService.getUser(id)).thenReturn(u1);

        mockMvc.perform(get("/user/view/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username", is("alice")))
                .andExpect(jsonPath("$.name", is("Alice A.")));

        verify(userService, times(1)).getUser(id);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getUser_NoUser() throws Exception {
        UUID id = UUID.randomUUID();

        when(userService.getUser(id)).thenThrow(new InvalidUserException(id.toString()));

        mockMvc.perform(get("/user/view/{id}", id))
                .andExpect(status().isConflict());

        verify(userService).getUser(id);
        verifyNoMoreInteractions(userService);
    }
}