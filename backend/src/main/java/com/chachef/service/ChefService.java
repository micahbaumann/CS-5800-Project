package com.chachef.service;

import com.chachef.dto.ChefCreateDto;
import com.chachef.entity.Chef;
import com.chachef.repository.ChefRepository;
import com.chachef.repository.UserRepository;
import com.chachef.service.exceptions.InvalidUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ChefService {

    @Autowired
    private ChefRepository chefRepository;

    @Autowired
    private UserRepository userRepository;

    public Chef createChef(ChefCreateDto chefCreateDto) {
        final Chef myChef = new Chef();

        myChef.setListingName(chefCreateDto.getListingName());
        myChef.setPrice(chefCreateDto.getPrice());
        if (!userRepository.findByUserId(chefCreateDto.getUser()).isPresent()) {
            throw new InvalidUserException(chefCreateDto.getUser().toString());
        }
        myChef.setUser(userRepository.findByUserId(chefCreateDto.getUser()).get());

        return chefRepository.save(myChef);
    }

    public List<Chef> getAllChefs() {
        return chefRepository.findAll();
    }

    public Chef getChefProfile(UUID id) {
        if (!chefRepository.findByChefId(id).isPresent()) {
            throw new InvalidUserException(id.toString());
        }
        return chefRepository.findByChefId(id).get();
    }
}
