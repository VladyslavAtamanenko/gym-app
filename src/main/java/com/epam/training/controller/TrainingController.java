package com.epam.training.controller;

import com.epam.training.dto.GetTrainingsByTraineeResponse;
import com.epam.training.dto.GetTrainingsByTrainerResponse;
import com.epam.training.dto.TrainingCreateRequest;
import com.epam.training.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Trainings", description = "Training session creation and history retrieval")
@RestController
@RequestMapping(value = "/trainings", produces = {"application/json", "application/xml"})
public class TrainingController {

    private final TrainingService trainingService;

    @Autowired
    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @Operation(
            summary = "Create a training session",
            description = "Records a new training session linking a trainee, a trainer, and a training type"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training session created"),
            @ApiResponse(responseCode = "400", description = "Validation failed — required fields missing or invalid"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "Access denied — authenticated user is not the trainee or trainer in this session"),
            @ApiResponse(responseCode = "404", description = "Trainee or trainer not found")
    })
    @PreAuthorize("@gymSecurity.isTrainingParticipant(#req, authentication)")
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Void> create(@Valid @RequestBody TrainingCreateRequest req) {
        trainingService.create(req);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get trainee's training history",
            description = "Returns a paged list of training sessions for a trainee. " +
                    "All filter parameters are optional. Page numbering starts at 1."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paged training list returned"),
            @ApiResponse(responseCode = "400", description = "Validation failed — page or size is less than 1, or from/to is not a valid ISO-8601 date (yyyy-MM-dd)"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "Access denied — not this trainee's account"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PreAuthorize("hasRole('TRAINEE') and @gymSecurity.isOwner(#username, authentication)")
    @GetMapping("/trainee/{username}")
    public ResponseEntity<PagedModel<EntityModel<GetTrainingsByTraineeResponse>>> getByTrainee(
            @Parameter(description = "Trainee's username", required = true, example = "John.Doe")
            @PathVariable String username,
            @Parameter(description = "Filter: sessions on or after this date (ISO-8601, e.g. 2025-01-01)", example = "2025-01-01")
            @RequestParam(required = false) LocalDate from,
            @Parameter(description = "Filter: sessions on or before this date (ISO-8601, e.g. 2025-12-31)", example = "2025-12-31")
            @RequestParam(required = false) LocalDate to,
            @Parameter(description = "Filter: trainer's full name", example = "Alice Smith")
            @RequestParam(required = false) String trainerName,
            @Parameter(description = "Filter: training type name", example = "Yoga")
            @RequestParam(required = false) String trainingType,
            @Parameter(description = "1-based page number", example = "1")
            @Min(1) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of items per page", example = "10")
            @Min(1) @RequestParam(defaultValue = "10") int size,
            @Parameter(hidden = true) PagedResourcesAssembler<GetTrainingsByTraineeResponse> assembler) {
        Page<GetTrainingsByTraineeResponse> result =
                trainingService.findByTrainee(username, from, to, trainerName, trainingType, pageOf(page, size));
        return ResponseEntity.ok(assembler.toModel(result));
    }

    @Operation(
            summary = "Get trainer's training history",
            description = "Returns a paged list of training sessions for a trainer. " +
                    "All filter parameters are optional. Page numbering starts at 1."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paged training list returned"),
            @ApiResponse(responseCode = "400", description = "Validation failed — page or size is less than 1, or from/to is not a valid ISO-8601 date (yyyy-MM-dd)"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "Access denied — not this trainer's account"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @PreAuthorize("hasRole('TRAINER') and @gymSecurity.isOwner(#username, authentication)")
    @GetMapping("/trainer/{username}")
    public ResponseEntity<PagedModel<EntityModel<GetTrainingsByTrainerResponse>>> getByTrainer(
            @Parameter(description = "Trainer's username", required = true, example = "Alice.Smith")
            @PathVariable String username,
            @Parameter(description = "Filter: sessions on or after this date (ISO-8601, e.g. 2025-01-01)", example = "2025-01-01")
            @RequestParam(required = false) LocalDate from,
            @Parameter(description = "Filter: sessions on or before this date (ISO-8601, e.g. 2025-12-31)", example = "2025-12-31")
            @RequestParam(required = false) LocalDate to,
            @Parameter(description = "Filter: trainee's full name", example = "John Doe")
            @RequestParam(required = false) String traineeName,
            @Parameter(description = "1-based page number", example = "1")
            @Min(1) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of items per page", example = "10")
            @Min(1) @RequestParam(defaultValue = "10") int size,
            @Parameter(hidden = true) PagedResourcesAssembler<GetTrainingsByTrainerResponse> assembler) {
        Page<GetTrainingsByTrainerResponse> result =
                trainingService.findByTrainer(username, from, to, traineeName, pageOf(page, size));
        return ResponseEntity.ok(assembler.toModel(result));
    }

    private static PageRequest pageOf(int page, int size) {
        return PageRequest.of(page - 1, size);
    }
}
