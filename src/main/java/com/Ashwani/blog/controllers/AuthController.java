package com.Ashwani.blog.controllers;

import com.Ashwani.blog.domain.dtos.AuthResponse;
import com.Ashwani.blog.domain.dtos.LoginRequest;
import com.Ashwani.blog.domain.dtos.SignUpRequest;
import com.Ashwani.blog.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        UserDetails userDetails = authenticationService.authenticate(
                loginRequest.getEmail(), loginRequest.getPassword());

        String tokenValue = authenticationService.generateToken(userDetails);

        AuthResponse authResponse = AuthResponse.builder()
                .token(tokenValue)
                .expiresIn(86400)
                .build();
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signUp(@RequestBody SignUpRequest signUpRequest) {
        // Register the user
        UserDetails userDetails = authenticationService.signUp(signUpRequest);

        // Generate JWT token immediately after signup
        String tokenValue = authenticationService.generateToken(userDetails);

        // Build response
        AuthResponse authResponse = AuthResponse.builder()
                .token(tokenValue)
                .expiresIn(86400) // same as login expiry
                .build();

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/verify-account")
    ResponseEntity<String> verifyAccount(@RequestParam String email,@RequestParam String otp){
    return new ResponseEntity<>(authenticationService.verifyAccount(email,otp), HttpStatus.OK);
    }

    @PostMapping("/regenerate-otp")
    public ResponseEntity<String> regenerateOtp(@RequestParam String email) {
        return new ResponseEntity<>(authenticationService.regenerateOtp(email), HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        return new ResponseEntity<>(authenticationService.forgotPassword(email), HttpStatus.OK);
    }

    @PostMapping("/set-password")
    public ResponseEntity<String> setPassword(@RequestParam String email,@RequestBody String newPassword) {
        return new ResponseEntity<>(authenticationService.setPassword(email, newPassword), HttpStatus.OK);
    }
}














