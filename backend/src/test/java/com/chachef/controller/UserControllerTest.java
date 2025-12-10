package com.chachef.controller;

import com.chachef.dataobjects.AuthContext;
import com.chachef.entity.User;
import com.chachef.service.UserService;
import com.chachef.service.exceptions.InvalidUserException;
import com.chachef.service.JwtService;
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

    @MockBean
    private JwtService jwtService;

    @Test
    void addUser() throws Exception {
        User saved = new User("jdoe", "John Doe", "");

        when(userService.createUser(any())).thenReturn(saved);

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                 { "username": "jdoe", "name": "John Doe", "password": "ignored" }
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
        User u1 = new User("alice", "Alice A.", "");
        User u2 = new User("bob", "Bob B.", "");
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
    void getUser_usesAuthContextUserIdAndReturnsUser() throws Exception {
        UUID authUserId = UUID.randomUUID();
        User user = new User("alice", "Alice A.", "");

        when(userService.getUser(authUserId)).thenReturn(user);

        mockMvc.perform(get("/user/view")
                        .requestAttr("auth", new AuthContext(authUserId, "alice", "Alice A.")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username", is("alice")))
                .andExpect(jsonPath("$.name", is("Alice A.")));

        verify(userService, times(1)).getUser(authUserId);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getUser_NoUser_returnsConflict() throws Exception {
        UUID authUserId = UUID.randomUUID();

        when(userService.getUser(authUserId))
                .thenThrow(new InvalidUserException(authUserId.toString()));

        mockMvc.perform(get("/user/view")
                        .requestAttr("auth", new AuthContext(authUserId, "alice", "Alice A.")))
                .andExpect(status().isConflict());

        verify(userService).getUser(authUserId);
        verifyNoMoreInteractions(userService);
    }
}
