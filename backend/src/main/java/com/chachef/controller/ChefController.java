package com.chachef.controller;

import com.chachef.dto.ChefCreateDto;
import com.chachef.entity.Chef;
import com.chachef.service.ChefService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/chef")
public class ChefController {

    @Autowired
    private ChefService chefService;

    @PostMapping("/create")
    public ResponseEntity<Void> addChef(@Valid @RequestBody ChefCreateDto chefCreateDto) {

        chefService.createChef(chefCreateDto);

//        return new ResponseEntity<>("made chef", HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/getChef")
    public ResponseEntity<List<Chef>> getChef() {
        return new ResponseEntity<>(chefService.getChef(), HttpStatus.OK);
    }
}
