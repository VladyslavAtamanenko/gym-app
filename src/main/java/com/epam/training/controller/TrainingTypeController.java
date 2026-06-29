package com.epam.training.controller;

import com.epam.training.dto.TrainingTypeDTO;
import com.epam.training.service.TrainingTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Training Types", description = "Read-only catalog of available training types")
@RestController
@RequestMapping(value = "/training-types", produces = {"application/json", "application/xml"})
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;

    @Autowired
    public TrainingTypeController(TrainingTypeService trainingTypeService) {
        this.trainingTypeService = trainingTypeService;
    }

    @Operation(
            summary = "List all training types",
            description = "Returns the complete list of training types available in the system"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List returned (may be empty)"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    })
    @PreAuthorize("hasAnyRole('TRAINEE', 'TRAINER')")
    @GetMapping
    public ResponseEntity<List<TrainingTypeDTO>> getAll() {
        return ResponseEntity.ok(trainingTypeService.findAll());
    }
}
