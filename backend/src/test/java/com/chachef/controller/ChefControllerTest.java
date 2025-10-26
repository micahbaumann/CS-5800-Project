package com.chachef.controller;

import com.chachef.entity.Chef;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import java.util.List;

import static com.chachef.TestHelper.*;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql(scripts = "/sample_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,  config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.INFERRED) // run within test tx
)
@Rollback(true)
@Transactional
class ChefControllerTest {

    @Autowired
    private ChefController chefController;

    @Autowired
    private EntityManager em;

    @Test
    void addChefNotNull() {
        Chef savedChef = chefController.addChef(sampleChefDto()).getBody();
        assertNotNull(savedChef);
    }

    @Test
    void addChefPersists() {
        Chef savedChef = chefController.addChef(sampleChefDto()).getBody();

        List<Chef> users = em.createQuery("SELECT c FROM Chef c WHERE c.chefId = :uuid", Chef.class)
                .setParameter("uuid", savedChef.getChefId())
                .getResultList();

        assertEquals(1, users.size());
    }

    @Test
    void getChef() {

    }
}