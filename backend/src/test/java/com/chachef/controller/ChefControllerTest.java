package com.chachef.controller;

import com.chachef.dataobjects.AuthContext;
import com.chachef.dto.ChefCreateDto;
import com.chachef.entity.Chef;
import com.chachef.service.ChefService;
import com.chachef.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChefController.class)
class ChefControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChefService chefService;

    // Needed so AuthFilter (which depends on JwtService) can be created in this slice
    @MockBean
    private JwtService jwtService;

    private UUID userId;
    private UUID chefId;
    private AuthContext authContext;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        chefId = UUID.randomUUID();
        authContext = new AuthContext(userId, "exampleUser", "Example User");
    }

    @Test
    @DisplayName("POST /chef/create -> 200 OK and returns saved chef")
    void addChef_ok() throws Exception {
        Chef chef = new Chef();
        chef.setListingName("My Listing");
        chef.setPrice(100.0);

        when(chefService.createChef(any(ChefCreateDto.class), any(AuthContext.class)))
                .thenReturn(chef);

        String payload = """
            {
              "price": 100.0,
              "listing_name": "My Listing"
            }
            """;

        mockMvc.perform(post("/chef/create")
                        .requestAttr("auth", authContext)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.listingName", is("My Listing")))
                .andExpect(jsonPath("$.price", is(100.0)));

        verify(chefService).createChef(any(ChefCreateDto.class), any(AuthContext.class));
    }

    @Test
    @DisplayName("POST /chef/create with invalid body -> 400 Bad Request")
    void addChef_validationError() throws Exception {
        // Assuming ChefCreateDto has validation constraints so "{}" is invalid
        mockMvc.perform(post("/chef/create")
                        .requestAttr("auth", authContext)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /chef/list -> 200 OK and returns list of chefs")
    void getAllChefs_ok() throws Exception {
        Chef c1 = new Chef();
        c1.setListingName("Chef A");
        c1.setPrice(50.0);

        Chef c2 = new Chef();
        c2.setListingName("Chef B");
        c2.setPrice(75.0);

        when(chefService.getAllChefs()).thenReturn(List.of(c1, c2));

        mockMvc.perform(get("/chef/list"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].listingName", is("Chef A")))
                .andExpect(jsonPath("$[1].listingName", is("Chef B")));

        verify(chefService).getAllChefs();
    }

    @Test
    @DisplayName("GET /chef/profile/{id} -> 200 OK and returns chef")
    void getChefProfile_ok() throws Exception {
        Chef chef = new Chef();
        chef.setListingName("Profile Chef");
        chef.setPrice(120.0);

        when(chefService.getChefProfile(chefId)).thenReturn(chef);

        mockMvc.perform(get("/chef/profile/{id}", chefId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.listingName", is("Profile Chef")))
                .andExpect(jsonPath("$.price", is(120.0)));

        verify(chefService).getChefProfile(chefId);
    }
}
