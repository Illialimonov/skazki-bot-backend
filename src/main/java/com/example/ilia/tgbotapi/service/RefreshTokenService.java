package com.example.ilia.tgbotapi.service;





import com.example.ilia.tgbotapi.models.RefreshToken;
import com.example.ilia.tgbotapi.models.User;
import com.example.ilia.tgbotapi.repo.RefreshTokenRepository;
import com.example.ilia.tgbotapi.repo.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    private final UserRepository userRepository;

    public RefreshToken createRefreshToken(User user){
        RefreshToken refreshToken = RefreshToken.builder()
                .owner(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(new Date(System.currentTimeMillis()+(86400000*7))) // set expiry of refresh token to 10 minutes - you can configure it application.properties file
                .build();
        return refreshTokenRepository.save(refreshToken);
    }


    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if(new Date(System.currentTimeMillis()).after(token.getExpiryDate())){
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token is expired. Please make a new login..!");
        }
        return token;
    }

}
