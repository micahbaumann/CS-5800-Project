package com.chachef.repository;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql(scripts = "/sample_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,  config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.INFERRED) // run within test tx
)
@Rollback(true)
@Transactional
class ChefRepositoryTest {

    @Test
    void getChefsById() {
    }
}