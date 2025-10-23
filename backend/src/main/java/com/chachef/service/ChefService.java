package com.chachef.service;

import com.chachef.entity.Chef;
import com.chachef.repository.ChefRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChefService {

    @Autowired
    private ChefRepository chefRepository;

    public void createChef() {
        final Chef myChef = new Chef();
        myChef.name = "Micah";

        chefRepository.save(myChef);
    }

    public List<Chef> getChef() {
        return chefRepository.findAll();
    }
}
