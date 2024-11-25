package com.example.ilia.tgbotapi.service;


import com.example.ilia.tgbotapi.models.User;
import com.example.ilia.tgbotapi.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;



    public User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (User) authentication.getPrincipal();
            return userRepository.findByTgId(userDetails.getUsername()).orElseThrow();
        } catch (RuntimeException e) {
            throw new ClassCastException("Not user found!");
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    public User getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }


    //00:00
    public ResponseEntity<?> collectBonus() {
        User user = getCurrentUser();
        if (LocalDateTime.now().isAfter(user.getLastDailyReward())){
            user.setCoins(user.getCoins()+50);
            user.setLastDailyReward(LocalDateTime.now().plusDays(1).with(LocalTime.MIDNIGHT));
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.OK).body("Bonus successfully rewarded!");
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, yyyy-MM-dd, HH:mm");
            String formattedDateTime = user.getLastDailyReward().format(formatter);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Next reward available: " + formattedDateTime);
        }
    }

    public ResponseEntity<?> monthlySub() {
        User user = getCurrentUser();
        user.setSubscriptionExpirationDate(LocalDateTime.now().plusMonths(1));
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK).body("Subscription was successfully extended by 1 month!");
    }

    public ResponseEntity<?> yearlySub() {
        User user = getCurrentUser();
        user.setSubscriptionExpirationDate(LocalDateTime.now().plusYears(1));
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK).body("Subscription was successfully extended by 1 year!");
    }
}
