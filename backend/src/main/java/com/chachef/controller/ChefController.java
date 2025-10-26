package com.chachef.controller;

import com.chachef.dto.ChefCreateDto;
import com.chachef.entity.Chef;
import com.chachef.service.ChefService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/chef")
public class ChefController {

    @Autowired
    private ChefService chefService;

    @PostMapping("/create")
    public ResponseEntity<Chef> addChef(@Valid @RequestBody ChefCreateDto chefCreateDto) {

        Chef savedChef = chefService.createChef(chefCreateDto);

//        return new ResponseEntity<>("made chef", HttpStatus.OK);
        return new ResponseEntity<>(savedChef, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Chef>> getChef() {
        return new ResponseEntity<>(chefService.getAllChefs(), HttpStatus.OK);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<Chef> getChefProfile(@PathVariable UUID id) {
        return new ResponseEntity<>(chefService.getChefProfile(id), HttpStatus.OK);
    }
}
