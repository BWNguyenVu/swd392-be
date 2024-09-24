package com.example.myflower.controller;

import com.example.myflower.dto.auth.requests.*;
import com.example.myflower.dto.auth.responses.*;
import com.example.myflower.service.impl.AuthServiceImpl;
import com.example.myflower.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {
    @Autowired
    private AccountUtils accountUtils;

    @Autowired
    private AuthServiceImpl authService;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome XD";
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return authService.checkLogin(loginRequestDTO);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> registerAccount(@RequestBody RegisterRequestDTO registerRequestDTO) {
        return authService.registerAccount(registerRequestDTO);
    }

    @GetMapping("/verify/{token}")
    public ResponseEntity<Void> activateAccount(@PathVariable String token) throws Exception {
        if (authService.verifyAccount(token)) {
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("http://localhost:8080/auth/welcome")).build();
        }
        return null;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponseDTO> forgotPassword(@RequestBody ForgotPasswordRequestDTO forgotPasswordRequest) {
        return authService.forgotPassword(forgotPasswordRequest);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponseDTO> resetPassword(@RequestParam("token") String token,
                                                                  @RequestBody ResetPasswordRequestDTO resetPasswordRequest) {
        return authService.resetPassword(resetPasswordRequest, token);

    }

    @PostMapping("/change-password")
    public ResponseEntity<ChangePasswordResponseDTO> changePassword(@RequestHeader("Authorization") String token,
                                                                    @RequestBody ChangePasswordRequestDTO changePasswordRequest) {
        return authService.changePassword(changePasswordRequest);
    }
}
