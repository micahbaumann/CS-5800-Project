package com.chachef.service;

import com.chachef.dto.ChefCreateDto;
import com.chachef.entity.Chef;
import com.chachef.repository.ChefRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import com.chachef.TestHelper;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql(scripts = "/sample_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ChefServiceTest {

    @Autowired
    private ChefService chefService;

    @Autowired
    private ChefRepository chefRepository;

    private ChefCreateDto sampleChefDto() {
        final ChefCreateDto chefDto = new ChefCreateDto(TestHelper.SAMPLE_USER_UUID, 50.0, "Pietro");

        return chefDto;
    }

    @Test
    void createChef() {

    }

    @Test
    void getChef() {
    }
}