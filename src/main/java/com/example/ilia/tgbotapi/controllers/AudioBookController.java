package com.example.ilia.tgbotapi.controllers;

import com.example.ilia.tgbotapi.models.AudioBook;
import com.example.ilia.tgbotapi.models.User;
import com.example.ilia.tgbotapi.service.AudioBookService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/audiobook")
public class AudioBookController {
    private final AudioBookService service;

    @PostMapping
    public AudioBook createAudioBook(@RequestBody AudioBook audioBook) {
        return service.createAudioBook(audioBook);
    }

    @GetMapping("/{id}")
    public AudioBook getAudioBookById(@PathVariable String id) {
        return service.getAudioBookById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteAudioBook(@PathVariable String id) {
        service.deleteAudioBook(id);
    }

    @GetMapping("/all")
    public List<AudioBook> allAudioBooks() {
        return service.getAllAudioBooks();
    }

}
