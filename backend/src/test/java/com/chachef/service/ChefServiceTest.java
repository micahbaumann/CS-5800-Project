package com.chachef.service;

import com.chachef.dto.ChefCreateDto;
import com.chachef.entity.Chef;
import com.chachef.repository.ChefRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import static com.chachef.TestHelper.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql(scripts = "/sample_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,  config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.INFERRED) // run within test tx
)
@Rollback(true)
@Transactional
class ChefServiceTest {

    @Autowired
    private ChefService chefService;

    @Test
    void createChef() {
        final ChefCreateDto chefDto = sampleChefDto();

        Chef savedChef = chefService.createChef(chefDto);

        assertNotNull(savedChef);
    }

    @Test
    void getAllChefs() {
        boolean chefExists =
            chefService.getAllChefs().stream()
                .anyMatch(chef -> {
                    return chef.getChefId().equals(SAMPLE_CHEF_UUID);
                });

        assertTrue(chefExists);
    }
}