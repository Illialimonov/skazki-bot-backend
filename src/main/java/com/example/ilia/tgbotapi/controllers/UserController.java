package com.example.ilia.tgbotapi.controllers;

import com.example.ilia.tgbotapi.models.User;
import com.example.ilia.tgbotapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;


    //TODO Operation annotation for swagger
    @Operation(summary = "return the email of the current user (jwt token owner)")
    @GetMapping("/current")
    public String currentUserName() {
        return userService.getCurrentUser().getTgId();
    }

    //TODO Operation annotation for swagger
    @Operation(summary = "NEW: collect a bonus once every 24 hours")
    @PostMapping("/collect-bonus")
    public ResponseEntity<?> collectBonus() {
        return userService.collectBonus();
    }

    @Operation(summary = "NEW")
    @PostMapping("monthly-sub")
    public ResponseEntity<?> monthlySub() {
        return userService.monthlySub();
    }


    @Operation(summary = "NEW")
    @PostMapping("yearly-sub")
    public ResponseEntity<?> yearlySub() {
        return userService.yearlySub();
    }



    @GetMapping("/{id}")
    public User getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
    }

    @GetMapping("/all")
    public List<User> allUsers() {
        return userService.getAllUsers();
    }
}
