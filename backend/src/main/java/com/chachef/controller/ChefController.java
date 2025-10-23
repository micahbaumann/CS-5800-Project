package com.chachef.controller;

import com.chachef.entity.Chef;
import com.chachef.service.ChefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller("/chef")
public class ChefController {

    @Autowired
    private ChefService chefService;

    @GetMapping("/addChef")
    public ResponseEntity<String> addChef() {

        chefService.createChef();

        return new ResponseEntity<>("made chef", HttpStatus.OK);
    }

    @GetMapping("/getChef")
    public ResponseEntity<List<Chef>> getChef() {
        return new ResponseEntity<>(chefService.getChef(), HttpStatus.OK);
    }
}
