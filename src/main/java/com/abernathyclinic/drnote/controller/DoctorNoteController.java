package com.abernathyclinic.drnote.controller;

import com.abernathyclinic.drnote.model.DoctorNote;
import com.abernathyclinic.drnote.model.DoctorNotes;
import com.abernathyclinic.drnote.repository.DoctorNoteRepository;
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
public class DoctorNoteController {
    @Autowired
    private DoctorNoteRepository doctorNoteRepository;

    @GetMapping("/get")
    public ResponseEntity<DoctorNote> viewPatHistory(@RequestParam("patId") String patId) {
        ResponseEntity<DoctorNote> responseEntity;
        DoctorNote doctorNote;

        try {
            if (doctorNoteRepository.findByPatId(patId) != null) {
                doctorNote = doctorNoteRepository.findByPatId(patId);
                responseEntity = ResponseEntity.status(HttpStatus.OK).body(doctorNote);

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

    @GetMapping("/get/doctornotes")
    public ResponseEntity<DoctorNotes> getDoctorNotes() {
        ResponseEntity<DoctorNotes> responseEntity;
        DoctorNotes doctorNotes = new DoctorNotes();

        try {
            List<DoctorNote> fetchDoctorNotes = doctorNoteRepository.findAll();
            doctorNotes.setDoctorNotes(fetchDoctorNotes);

            responseEntity = ResponseEntity.status(HttpStatus.OK).body(doctorNotes);

            log.info("Processing request handling /get/patids...");
        } catch (RuntimeException ex) {
            log.error("Unable to fetch Dr Notes from DB");
            log.error(ex.getMessage());

            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return responseEntity;
    }

    @PostMapping("/add")
    public ResponseEntity<DoctorNote> addPathHistory(@RequestParam("patId") String patId, @RequestParam("note") String note) {
        List<String> notes = new ArrayList<>();
        ResponseEntity<DoctorNote> responseEntity;
        DoctorNote doctorNote;

        log.info("Post request handling.../patHistory/add");

        if (doctorNoteRepository.findByPatId(patId) != null) {
            doctorNote = doctorNoteRepository.findByPatId(patId);
        } else {
            doctorNote = DoctorNote.builder()
                    .patId(patId)
                    .notes(notes)
                    .build();
        }

        try {
            doctorNote.addNote(note);
            doctorNoteRepository.save(doctorNote);

            responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(doctorNote);

            log.info("Saved drNote: {}", doctorNote);
        } catch (RuntimeException ex) {
            log.error("Unable to saved Dr Note: {}", doctorNote);
            log.error(ex.getMessage());

            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return responseEntity;
    }
}
