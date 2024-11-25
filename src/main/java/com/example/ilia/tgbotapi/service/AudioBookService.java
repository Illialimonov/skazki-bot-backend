package com.example.ilia.tgbotapi.service;

import com.example.ilia.tgbotapi.models.AudioBook;
import com.example.ilia.tgbotapi.models.User;
import com.example.ilia.tgbotapi.repo.AudioBookRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AudioBookService {
    private final AudioBookRepository audioBookRepository;

    public List<AudioBook> getAllAudioBooks() {
        return audioBookRepository.findAll();
    }

    public AudioBook createAudioBook(AudioBook audioBook) {
        return audioBookRepository.save(audioBook);
    }

    public AudioBook getAudioBookById(String id) {
        return audioBookRepository.findById(id).orElse(null);
    }

    public void deleteAudioBook(String id) {
        audioBookRepository.deleteById(id);
    }

}
