package com.example.ilia.tgbotapi.auth;


import com.example.ilia.tgbotapi.config.JwtService;
import com.example.ilia.tgbotapi.models.RefreshToken;
import com.example.ilia.tgbotapi.models.User;

import com.example.ilia.tgbotapi.repo.RefreshTokenRepository;
import com.example.ilia.tgbotapi.repo.UserRepository;
import com.example.ilia.tgbotapi.service.RefreshTokenService;
import com.example.ilia.tgbotapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 6;
    private static final Random RANDOM = new SecureRandom();
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;


    public void register(String tgId) {
        User user = new User();
        user.setTgId(tgId);
        user.setRole("ROLE_USER");
        user.setCoins(500);
        user.setTier("Basic");
        user.setLastDailyReward(LocalDateTime.now().minusHours(24));


        userRepository.save(user);
    }


    public ResponseEntity<AuthenticationResponse> authenticate(RegisterRequest request) {
        if (!userRepository.existsByTgId(request.getTgId())) register(request.getTgId());

        var user = userRepository.findByTgId(request.getTgId()).orElseThrow(() -> new UsernameNotFoundException("User with tgId " + request.getTgId() + " was not found"));

        var jwtToken = jwtService.generateToken(user);

        refreshTokenRepository.deleteByOwnerId(user.getId());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);


        HashMap<String, String> userDetails = new HashMap<>();
        userDetails.put("id", user.getId());
        userDetails.put("refreshToken", refreshToken.getToken());
        userDetails.put("role", user.getRole());
        userDetails.put("tier", user.getTier());
        userDetails.put("coins", String.valueOf(user.getCoins()));
        userDetails.put("subscriptionExpirationDate", String.valueOf(user.getSubscriptionExpirationDate()));

        // Create a cookie with the JWT token
        ResponseCookie cookie = ResponseCookie.from("jwt", jwtToken) // Set cookie name and value
                .httpOnly(true) // Prevents JavaScript access to the cookie
                .secure(true) // Ensures the cookie is sent only over HTTPS
                .path("/") // Makes the cookie available on all pages of the website
                .maxAge(3600) // Sets the cookie to expire in 1 hour
                .sameSite("Lax") // Helps mitigate CSRF attacks
                .build();


        AuthenticationResponse response = AuthenticationResponse.builder()
                .access_token(jwtToken)
                .refresh_token(refreshToken.getToken())
                .userDetails(userDetails)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString()) // Add the cookie to the response
                .body(response); // Return the response body


    }



    public ResponseEntity<AuthenticationResponse> refreshToken(RefreshTokenRequestDTO refreshTokenRequestDTO) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenService.findByToken(refreshTokenRequestDTO.getToken());

        if (optionalRefreshToken.isEmpty()) {
            // If the refresh token is not found, throw an exception
            throw new RuntimeException("Refresh Token is not in DB..!!");
        }

        // Get the refresh token from the Optional
        RefreshToken refreshToken = optionalRefreshToken.get();

        // Verify the expiration of the refresh token
        try {
            refreshToken = refreshTokenService.verifyExpiration(refreshToken);
        } catch (RuntimeException e) {
            // If the refresh token is expired, an exception will be thrown
            throw new RuntimeException(e.getMessage());
        }

        // Get the user information associated with the refresh token
        User user = refreshToken.getOwner();
        // Generate a new access token using the user's username
        String accessToken = jwtService.generateToken(user);


        // Create and return the response containing the new access token and the refresh token
        HashMap<String, String> userDetails = new HashMap<>();
        userDetails.put("id", user.getId());
        userDetails.put("refreshToken", refreshToken.getToken());
        userDetails.put("role", user.getRole());
        userDetails.put("tier", user.getTier());
        userDetails.put("coins", String.valueOf(user.getCoins()));
        userDetails.put("subscriptionExpirationDate", String.valueOf(user.getSubscriptionExpirationDate()));


        //fixed

        AuthenticationResponse response = AuthenticationResponse.builder()
                .access_token(accessToken)
                .refresh_token(refreshToken.getToken())
                .userDetails(userDetails)
                .build();

        ResponseCookie cookie = ResponseCookie.from("jwt", accessToken) // Set cookie name and value
                .httpOnly(true) // Prevents JavaScript access to the cookie
                .secure(true) // Ensures the cookie is sent only over HTTPS
                .path("/") // Makes the cookie available on all pages of the website
                .maxAge(3600) // Sets the cookie to expire in 1 hour
                .sameSite("Lax") // Helps mitigate CSRF attacks
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString()) // Add the cookie to the response
                .body(response); // Return the response body


    }


    public static String generateCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }

    public boolean userIsNotInDB(String email) {
        return userRepository.findByEmail(email).isEmpty();
    }





}
