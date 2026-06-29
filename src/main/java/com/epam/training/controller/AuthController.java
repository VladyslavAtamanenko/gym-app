package com.epam.training.controller;

import com.epam.training.dto.LoginRequest;
import com.epam.training.dto.LoginResponse;
import com.epam.training.security.JwtUtil;
import com.epam.training.security.LoginAttemptService;
import com.epam.training.security.TokenBlacklist;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "JWT login and logout")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final LoginAttemptService loginAttemptService;
    private final TokenBlacklist tokenBlacklist;

    @Operation(summary = "Login", description = "Authenticates with username and password; returns a JWT Bearer token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful — JWT returned"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "429", description = "Account locked due to 3 consecutive failed logins (unlocks after 5 minutes)")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String username = request.getUsername();

        if (loginAttemptService.isBlocked(username)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new LoginResponse(null, "Account locked for 5 minutes due to repeated failed logins"));
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, request.getPassword()));
        } catch (BadCredentialsException e) {
            loginAttemptService.loginFailed(username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(null, "Invalid credentials"));
        }

        loginAttemptService.loginSucceeded(username);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return ResponseEntity.ok(new LoginResponse(jwtUtil.generate(userDetails), null));
    }

    @Operation(summary = "Logout", description = "Invalidates the current JWT token; subsequent requests with this token will be rejected")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            tokenBlacklist.add(authHeader.substring(7));
        }
        return ResponseEntity.noContent().build();
    }
}
