package com.chachef.service;

import com.chachef.dataobjects.AuthContext;
import com.chachef.dto.ChefCreateDto;
import com.chachef.entity.Chef;
import com.chachef.entity.User;
import com.chachef.repository.ChefRepository;
import com.chachef.repository.UserRepository;
import com.chachef.service.exceptions.InvalidUserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChefServiceTest {

    @Mock
    private ChefRepository chefRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChefService chefService;

    @Test
    void createChef() {
        UUID missingUserId = UUID.randomUUID();
        ChefCreateDto dto = new ChefCreateDto(10.0, "Test Listing");
        AuthContext authContext = new AuthContext(missingUserId, "exampleUser", "Example User");

        when(userRepository.findByUserId(missingUserId)).thenReturn(Optional.empty());

        assertThrows(InvalidUserException.class, () -> chefService.createChef(dto, authContext));

        verify(chefRepository, never()).save(any(Chef.class));
    }

    @Test
    void createChef_InvalidUserException() {
        UUID missingChefId = UUID.randomUUID();
        when(chefRepository.findByChefId(missingChefId)).thenReturn(Optional.empty());

        assertThrows(InvalidUserException.class, () -> chefService.getChefProfile(missingChefId));

        verify(chefRepository, times(1)).findByChefId(missingChefId);
        verifyNoMoreInteractions(chefRepository);
    }

    @Test
    void getAllChefs() {
        Chef c1 = new Chef();
        Chef c2 = new Chef();
        when(chefRepository.findAll()).thenReturn(List.of(c1, c2));

        List<Chef> result = chefService.getAllChefs();

        assertEquals(2, result.size());
        assertSame(c1, result.get(0));
        assertSame(c2, result.get(1));
        verify(chefRepository).findAll();
        verifyNoMoreInteractions(chefRepository);
        verifyNoInteractions(userRepository);
    }

    @Test
    void getChefProfile() {
        UUID userId = UUID.randomUUID();

        User user = new User("jdoe", "John Doe", "12345");
        AuthContext authContext = new AuthContext(userId, "exampleUser", "Example User");

        ChefCreateDto dto = new ChefCreateDto(25.0, "Sample Listing");

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(chefRepository.save(any(Chef.class))).thenAnswer(inv -> inv.getArgument(0));

        Chef saved = chefService.createChef(dto, authContext);

        assertNotNull(saved);
        assertSame(user, saved.getUser());
        assertEquals("Sample Listing", saved.getListingName());
        assertEquals(25.0, saved.getPrice());

        verify(userRepository, times(2)).findByUserId(userId);
        verify(chefRepository).save(any(Chef.class));
    }

    @Test
    void getChefProfile_InvalidUserException() {
        UUID userId = UUID.randomUUID();

        ChefCreateDto dto = new ChefCreateDto(25.0, "Sample Listing");
        AuthContext authContext = new AuthContext(userId, "exampleUser", "Example User");

        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(InvalidUserException.class, () -> chefService.createChef(dto, authContext));

        verify(userRepository).findByUserId(userId);
        verifyNoInteractions(chefRepository);
    }
}