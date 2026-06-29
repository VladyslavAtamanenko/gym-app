package com.epam.training.controller;

import com.epam.training.dto.*;
import com.epam.training.service.TrainerService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Trainers", description = "Trainer registration, profile management, and activation")
@RestController
@RequestMapping(value = "/trainers", produces = {"application/json", "application/xml"})
public class TrainerController {

    private final TrainerService trainerService;

    @Autowired
    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @Operation(
            summary = "Change trainer password",
            description = "Updates the trainer's password after verifying the old one"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed — any field is blank"),
            @ApiResponse(responseCode = "401", description = "Old password does not match or missing JWT"),
            @ApiResponse(responseCode = "403", description = "Access denied — not this trainer's account"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @PreAuthorize("hasRole('TRAINER') and @gymSecurity.isOwner(#username, authentication)")
    @PutMapping(value = "/{username}/password", consumes = "application/json")
    public ResponseEntity<Void> changePassword(
            @Parameter(description = "Trainer's username", required = true, example = "Alice.Smith")
            @PathVariable String username,
            @Valid @RequestBody ChangeLoginRequest req) {
        boolean changed = trainerService.changePassword(req);
        return changed ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @Operation(
            summary = "Register a new trainer",
            description = "Creates a trainer profile and returns auto-generated login credentials"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Trainer registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed — firstName, lastName, or specialization is blank")
    })
    @PostMapping(consumes = "application/json")
    public ResponseEntity<TrainerCreateResponse> register(@Valid @RequestBody TrainerCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trainerService.create(req));
    }

    @Operation(
            summary = "Get trainer profile",
            description = "Returns the full profile of the trainer identified by username"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer profile returned"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "Access denied — not this trainer's account"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @PreAuthorize("hasRole('TRAINER') and @gymSecurity.isOwner(#username, authentication)")
    @GetMapping("/{username}")
    public ResponseEntity<TrainerGetResponse> getByUsername(
            @Parameter(description = "Trainer's username", required = true, example = "Alice.Smith")
            @PathVariable String username) {
        return ResponseEntity.ok(trainerService.findByUsername(username));
    }

    @Operation(
            summary = "Update trainer profile",
            description = "Replaces the trainer's profile fields; all fields in the request body are required"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "Access denied — not this trainer's account"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @PreAuthorize("hasRole('TRAINER') and @gymSecurity.isOwner(#username, authentication)")
    @PutMapping(value = "/{username}", consumes = "application/json")
    public ResponseEntity<TrainerUpdateResponse> update(
            @Parameter(description = "Trainer's username", required = true, example = "Alice.Smith")
            @PathVariable String username,
            @Valid @RequestBody TrainerUpdateRequest req) {
        return ResponseEntity.ok(trainerService.update(username, req));
    }

    @Operation(
            summary = "Get active trainers not assigned to a trainee",
            description = "Returns a paged list of active trainers who have not yet been assigned to the given trainee. " +
                    "Page numbering starts at 1."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paged list of trainers returned"),
            @ApiResponse(responseCode = "400", description = "Validation failed — page or size is less than 1, or pagination parameters are not valid integers"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "Access denied — not this trainee's account"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PreAuthorize("hasRole('TRAINEE') and @gymSecurity.isOwner(#username, authentication)")
    @GetMapping("/not-assigned/{username}")
    public ResponseEntity<PagedModel<EntityModel<TrainerDTO>>> getNotAssignedOnTrainee(
            @Parameter(description = "Trainee's username", required = true, example = "John.Doe")
            @PathVariable String username,
            @Parameter(description = "1-based page number", example = "1")
            @Min(1) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of items per page", example = "10")
            @Min(1) @RequestParam(defaultValue = "10") int size,
            @Parameter(hidden = true) PagedResourcesAssembler<TrainerDTO> assembler) {
        Page<TrainerDTO> result = trainerService.findNotAssignedOnTrainee(username, pageOf(page, size));
        return ResponseEntity.ok(assembler.toModel(result));
    }

    @Operation(
            summary = "Activate or deactivate trainer",
            description = "Sets the trainer's active status; pass isActive=true to activate, false to deactivate"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "400", description = "isActive field is null"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "Access denied — not this trainer's account"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @PreAuthorize("hasRole('TRAINER') and @gymSecurity.isOwner(#username, authentication)")
    @PatchMapping(value = "/{username}", consumes = "application/json")
    public ResponseEntity<Void> setActive(
            @Parameter(description = "Trainer's username", required = true, example = "Alice.Smith")
            @PathVariable String username,
            @Valid @RequestBody ActivateRequest req) {
        if (Boolean.TRUE.equals(req.getIsActive())) {
            trainerService.activate(username);
        } else {
            trainerService.deactivate(username);
        }
        return ResponseEntity.ok().build();
    }

    private static PageRequest pageOf(int page, int size) {
        return PageRequest.of(page - 1, size);
    }
}
