package com.example.ilia.tgbotapi.controllers;


import com.example.ilia.tgbotapi.auth.AuthenticationResponse;
import com.example.ilia.tgbotapi.auth.AuthenticationService;
import com.example.ilia.tgbotapi.auth.RefreshTokenRequestDTO;
import com.example.ilia.tgbotapi.auth.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class TelegramAuthController {
    private final AuthenticationService service;

    @Operation(summary = "pass email only. will authenticate if user exists, or (register + authenticate) if user doesn't exist. jwt is also passed to cookies")
    @PostMapping("/telegram-auth")
    public ResponseEntity<AuthenticationResponse> authenticateTelegramUser(@RequestBody RegisterRequest request) {
        return service.authenticate(request);
    }

    @Operation(summary = "refreshtoken")
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        return service.refreshToken(refreshTokenRequestDTO);
    }

    @GetMapping("/hi")
    public String hi() {
        return "hi!!!";
    }


}
