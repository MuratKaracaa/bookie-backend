package com.karacam.bookie.controllers;

import com.karacam.bookie.core.enums.FlowStage;
import com.karacam.bookie.core.flow.FlowStep;
import com.karacam.bookie.dtos.request.*;
import com.karacam.bookie.dtos.response.AuthResponse;
import com.karacam.bookie.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final String REGISTRATION_FLOW = "REGISTRATION_FLOW";
    private final String RESET_PASSWORD_FLOW = "RESET_PASSWORD";
    private final String CHANGE_PASSWORD_FLOW = "CHANGE_PASSWORD";
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @FlowStep(key = REGISTRATION_FLOW, stage = FlowStage.INIT, anonymousFlow = true)
    @PostMapping("/register")
    public AuthResponse registerUser(@Valid @RequestBody RegisterUserRequest requestBody) {
        return this.authService.registerUser(requestBody);
    }

    @FlowStep(key = REGISTRATION_FLOW, stage = FlowStage.CONFIRMATION, anonymousFlow = true)
    @PostMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@Valid @RequestBody OTPRequest requestBody) {
        return this.authService.verifyEmail(requestBody);
    }

    @FlowStep(key = RESET_PASSWORD_FLOW, stage = FlowStage.INIT, anonymousFlow = true)
    @PostMapping("/reset-password-init")
    public AuthResponse resetPasswordInit(@Valid @RequestBody ResetPasswordInitRequest requestBody) {
        return this.authService.resetPasswordInit(requestBody);
    }

    @FlowStep(key = RESET_PASSWORD_FLOW, stage = FlowStage.INTERMEDIATE, anonymousFlow = true)
    @PostMapping("/reset-password-submit-new-password")
    public ResponseEntity<Void> resetPasswordSubmitNewPassword(@Valid @RequestBody ResetPasswordSubmitNewPasswordRequest requestBody) {
        return this.authService.resetPasswordSubmitNewPassword(requestBody);
    }

    @FlowStep(key = RESET_PASSWORD_FLOW, stage = FlowStage.CONFIRMATION, anonymousFlow = true)
    @PostMapping("/reset-password-confirm-new-password")
    public ResponseEntity<Void> resetPasswordConfirmNewPassword(@Valid @RequestBody OTPRequest requestBody) {
        return this.authService.resetPasswordConfirmNewPassword(requestBody);
    }

    @FlowStep(key = CHANGE_PASSWORD_FLOW, stage = FlowStage.INIT)
    @PostMapping("/change-password-init")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest requestBody) {
        return this.authService.changePasswordInit(requestBody);
    }


    @FlowStep(key = CHANGE_PASSWORD_FLOW, stage = FlowStage.CONFIRMATION)
    @PostMapping("/change-password-confirm")
    public ResponseEntity<Void> verifyNewPassword(@Valid @RequestBody OTPRequest requestBody) {
        return this.authService.changePasswordConfirm(requestBody);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest requestBody) {
        return this.authService.login(requestBody);
    }
}
