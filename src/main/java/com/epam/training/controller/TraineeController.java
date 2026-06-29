package com.epam.training.controller;

import com.epam.training.dto.*;
import com.epam.training.service.TraineeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Trainees", description = "Trainee registration, profile management, and activation")
@RestController
@RequestMapping(value = "/trainees", produces = {"application/json", "application/xml"})
public class TraineeController {

    private final TraineeService traineeService;

    @Autowired
    public TraineeController(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @Operation(
            summary = "Change trainee password",
            description = "Updates the trainee's password after verifying the old one"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed — any field is blank"),
            @ApiResponse(responseCode = "401", description = "Old password does not match or missing JWT"),
            @ApiResponse(responseCode = "403", description = "Access denied — not this trainee's account"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PreAuthorize("hasRole('TRAINEE') and @gymSecurity.isOwner(#username, authentication)")
    @PutMapping(value = "/{username}/password", consumes = "application/json")
    public ResponseEntity<Void> changePassword(
            @Parameter(description = "Trainee's username", required = true, example = "John.Doe")
            @PathVariable String username,
            @Valid @RequestBody ChangeLoginRequest req) {
        boolean changed = traineeService.changePassword(req);
        return changed ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @Operation(
            summary = "Register a new trainee",
            description = "Creates a trainee profile and returns auto-generated login credentials"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Trainee registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed — firstName or lastName is blank, or dateOfBirth is not a valid ISO-8601 date (yyyy-MM-dd)")
    })
    @PostMapping(consumes = "application/json")
    public ResponseEntity<TraineeCreateResponse> register(@Valid @RequestBody TraineeCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(traineeService.create(req));
    }

    @Operation(
            summary = "Get trainee profile",
            description = "Returns the full profile of the trainee identified by username"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee profile returned"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "Access denied — not this trainee's account"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PreAuthorize("hasRole('TRAINEE') and @gymSecurity.isOwner(#username, authentication)")
    @GetMapping("/{username}")
    public ResponseEntity<TraineeGetResponse> getByUsername(
            @Parameter(description = "Trainee's username", required = true, example = "John.Doe")
            @PathVariable String username) {
        return ResponseEntity.ok(traineeService.findByUsername(username));
    }

    @Operation(
            summary = "Update trainee profile",
            description = "Replaces the trainee's profile fields; all fields in the request body are required"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "Access denied — not this trainee's account"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PreAuthorize("hasRole('TRAINEE') and @gymSecurity.isOwner(#username, authentication)")
    @PutMapping(value = "/{username}", consumes = "application/json")
    public ResponseEntity<TraineeUpdateResponse> update(
            @Parameter(description = "Trainee's username", required = true, example = "John.Doe")
            @PathVariable String username,
            @Valid @RequestBody TraineeUpdateRequest req) {
        return ResponseEntity.ok(traineeService.update(username, req));
    }

    @Operation(
            summary = "Update trainee's trainer list",
            description = "Replaces the full list of trainers assigned to this trainee"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer list updated; returns the new list"),
            @ApiResponse(responseCode = "400", description = "Trainer usernames list is empty or missing"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "Access denied — not this trainee's account"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PreAuthorize("hasRole('TRAINEE') and @gymSecurity.isOwner(#username, authentication)")
    @PutMapping(value = "/{username}/trainers", consumes = "application/json")
    public ResponseEntity<List<TrainerDTO>> updateTrainersList(
            @Parameter(description = "Trainee's username", required = true, example = "John.Doe")
            @PathVariable String username,
            @Valid @RequestBody TraineeUpdateTrainersRequest req) {
        return ResponseEntity.ok(traineeService.updateTrainersList(username, req));
    }

    @Operation(
            summary = "Delete trainee",
            description = "Permanently removes the trainee profile and all associated trainings"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "Access denied — not this trainee's account"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PreAuthorize("hasRole('TRAINEE') and @gymSecurity.isOwner(#username, authentication)")
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Trainee's username", required = true, example = "John.Doe")
            @PathVariable String username) {
        traineeService.delete(username);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Activate or deactivate trainee",
            description = "Sets the trainee's active status; pass isActive=true to activate, false to deactivate"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "400", description = "isActive field is null"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "Access denied — not this trainee's account"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PreAuthorize("hasRole('TRAINEE') and @gymSecurity.isOwner(#username, authentication)")
    @PatchMapping(value = "/{username}", consumes = "application/json")
    public ResponseEntity<Void> setActive(
            @Parameter(description = "Trainee's username", required = true, example = "John.Doe")
            @PathVariable String username,
            @Valid @RequestBody ActivateRequest req) {
        if (Boolean.TRUE.equals(req.getIsActive())) {
            traineeService.activate(username);
        } else {
            traineeService.deactivate(username);
        }
        return ResponseEntity.ok().build();
    }
}
