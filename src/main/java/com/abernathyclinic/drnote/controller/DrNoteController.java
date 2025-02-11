package com.abernathyclinic.drnote.controller;

import com.abernathyclinic.drnote.model.DrNote;
import com.abernathyclinic.drnote.repository.DrNoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/patHistory")
public class DrNoteController {
    @Autowired
    private DrNoteRepository drNoteRepository;

    @GetMapping("/get")
    public ResponseEntity<DrNote> viewPatHistory(@RequestParam("patId") String patId) {
        ResponseEntity<DrNote> responseEntity;
        DrNote drNote;

        try {
            if (drNoteRepository.findByPatId(patId) != null) {
                drNote = drNoteRepository.findByPatId(patId);
                responseEntity = ResponseEntity.status(HttpStatus.OK).body(drNote);

                log.info("Get request handling... /patHistory/get");
            } else {
                log.info("Unable to find the patId: {}", patId);

                responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (RuntimeException ex) {
            log.error("Unable to fetch from Database with patID: {}", patId);
            log.error(ex.getMessage());

            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return responseEntity;
    }

    @GetMapping("/get/drnotes")
    public ResponseEntity<List<DrNote>> getDrNotes() {
        ResponseEntity<List<DrNote>> responseEntity;
        List<DrNote> drNotesList = new ArrayList<>();

        try {
            drNotesList = drNoteRepository.findAll();
            responseEntity = ResponseEntity.status(HttpStatus.OK).body(drNotesList);

            log.info("Processing request handling /get/patids...");
        } catch (RuntimeException ex) {
            log.error("Unable to fetch Dr Notes from DB");
            log.error(ex.getMessage());

            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return responseEntity;
    }

    @PostMapping("/add")
    public ResponseEntity<DrNote> addPathHistory(@RequestParam("patId") String patId, @RequestParam("note") String note) {
        List<String> notes = new ArrayList<>();
        ResponseEntity<DrNote> responseEntity;
        DrNote drNote;

        log.info("Post request handling.../patHistory/add");

        if (drNoteRepository.findById(patId).isPresent()) {
            drNote = drNoteRepository.findById(patId).get();
        } else {
            drNote = DrNote.builder()
                    .patId(patId)
                    .notes(notes)
                    .build();
        }

        try {
            drNote.addNote(note);
            drNoteRepository.save(drNote);

            responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(drNote);

            log.info("Saved drNote: {}", drNote);
        } catch (RuntimeException ex) {
            log.error("Unable to saved Dr Note: {}", drNote);
            log.error(ex.getMessage());

            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return responseEntity;
    }
}
