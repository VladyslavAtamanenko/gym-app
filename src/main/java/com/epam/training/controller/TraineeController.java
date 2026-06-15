package com.epam.training.controller;

import com.epam.training.dto.*;
import com.epam.training.service.TraineeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/trainees", consumes = {"application/JSON"}, produces = {"application/JSON", "application/XML"})
public class TraineeController {

    @Autowired
    TraineeService traineeService;

    @PostMapping
    public ResponseEntity<TraineeCreateResponse> register(@RequestBody TraineeCreateRequest req){
        return null;
    }

    @GetMapping
    public ResponseEntity<TraineeGetResponse> getAll(){
        return null;
    }

    @GetMapping("/{username}")
    public ResponseEntity<TraineeGetResponse> getByUsername(@RequestParam String username){
        return null;
    }

    @PutMapping("/{username}")
    public ResponseEntity<TraineeUpdateResponse> update(@RequestParam String username,
                                                        @RequestBody TraineeUpdateRequest req){
        return null;
    }

    @PutMapping("/username/trainers")
    public ResponseEntity<TraineeGetResponse> updateTrainersList(@RequestBody TraineeUpdateTrainersRequest req){
        return null;
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> delete(@RequestParam String username){
        return null;
    }
}
