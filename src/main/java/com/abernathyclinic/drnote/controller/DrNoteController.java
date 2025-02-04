package com.abernathyclinic.drnote.controller;

import com.abernathyclinic.drnote.model.DrNote;
import com.abernathyclinic.drnote.repository.DrNoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/patHistory/add")
public class DrNoteController {
    @Autowired
    private DrNoteRepository drNoteRepository;

    @PostMapping
    public ResponseEntity<DrNote> addPathHistory(@RequestParam("patId") String patId, @RequestParam("note") String note) {
        List<String> notes = new ArrayList<>();
        ResponseEntity<DrNote> responseEntity;
        DrNote drNote;

        log.info("Post request handling.../patHistory/add");

        if (drNoteRepository.findById(patId).isPresent()) {
            drNote = drNoteRepository.findById(patId).get();
        } else {
            drNote = DrNote.builder()
                    .id(patId)
                    .notes(notes)
                    .build();
        }

        try {
            drNote.addNote(note);
            drNoteRepository.save(drNote);

            responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(drNote);

            log.info("Saved drNote: {}", drNote);
        } catch (RuntimeException ex) {
            log.info("Unable to saved Dr Note: {}", drNote);
            log.error(ex.getMessage());

            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return responseEntity;
    }
}
