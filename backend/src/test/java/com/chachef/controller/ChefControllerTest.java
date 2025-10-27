package com.chachef.controller;

import com.chachef.entity.Chef;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import java.util.List;
import java.util.UUID;

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
    void getAllChefs() {
        Chef chef1 = chefController.addChef(sampleChefDto()).getBody();
        Chef chef2 = chefController.addChef(sampleChefDto()).getBody();

        em.flush();
        em.clear();

        ResponseEntity<List<Chef>> response = chefController.getAllChefs();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<Chef> chefs = response.getBody();
        assertTrue(chefs.size() >= 2, "Should return at least the two created chefs");

        var chefIds = chefs.stream().map(Chef::getChefId).toList();
        assertTrue(chefIds.contains(chef1.getChefId()));
        assertTrue(chefIds.contains(chef2.getChefId()));
    }

    @Test
    void getChefProfileReturnChef() {
        Chef saved = chefController.addChef(sampleChefDto()).getBody();
        assertNotNull(saved);

        em.flush();
        em.clear();

        ResponseEntity<Chef> response = chefController.getChefProfile(saved.getChefId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(saved.getChefId(), response.getBody().getChefId());
    }

    @Test
    void getChefProfileReturnNotFound() {
        UUID randomId = UUID.randomUUID();

        assertThrows(Exception.class, () -> chefController.getChefProfile(randomId));
    }
}