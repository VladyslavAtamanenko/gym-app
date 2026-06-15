package com.epam.training.controller;

import com.epam.training.dto.*;
import com.epam.training.service.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/trainers", consumes = {"application/JSON"}, produces = {"application/JSON", "application/XML"})
public class TrainerController {

    @Autowired
    TrainerService trainerService;


    @PostMapping
    public ResponseEntity<TrainerCreateResponse> register(@RequestBody TrainerCreateRequest req){
        return null;
    }

    @GetMapping
    public ResponseEntity<TrainerGetResponse> getAll(){
        return null;
    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerGetResponse> getByUsername(@RequestParam String username){
        return null;
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<TrainerGetResponse>> getNotAssignedOnTrainee(@RequestParam String username){
        return null;
    }

    @PutMapping("/{username}")
    public ResponseEntity<TrainerUpdateResponse> update(@RequestParam String username, @RequestBody TrainerUpdateRequest req){
        return null;
    }


}
