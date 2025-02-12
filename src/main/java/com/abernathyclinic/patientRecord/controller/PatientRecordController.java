package com.abernathyclinic.patientRecord.controller;

import com.abernathyclinic.patientRecord.model.ClinicalNote;
import com.abernathyclinic.patientRecord.model.PatientRecord;
import com.abernathyclinic.patientRecord.model.PatientRecords;
import com.abernathyclinic.patientRecord.repository.PatientRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/patHistory")
@CrossOrigin(origins = "http://localhost:3000")
public class PatientRecordController {
    @Autowired
    private PatientRecordRepository patientRecordRepository;

    @GetMapping("/get")
    public ResponseEntity<PatientRecord> viewPatHistory(@RequestParam("patId") String patId) {
        ResponseEntity<PatientRecord> responseEntity;
        PatientRecord patientRecord;

        try {
            if (patientRecordRepository.findByPatId(patId) != null) {
                patientRecord = patientRecordRepository.findByPatId(patId);
                responseEntity = ResponseEntity.status(HttpStatus.OK).body(patientRecord);

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
    public ResponseEntity<PatientRecords> getDoctorNotes() {
        ResponseEntity<PatientRecords> responseEntity;
        PatientRecords patientRecords = new PatientRecords();

        try {
            List<PatientRecord> fetchPatientRecords = patientRecordRepository.findAll();
            patientRecords.setPatientRecords(fetchPatientRecords);

            responseEntity = ResponseEntity.status(HttpStatus.OK).body(patientRecords);

            log.info("Processing request handling /get/patids...");
        } catch (RuntimeException ex) {
            log.error("Unable to fetch Dr Notes from DB");
            log.error(ex.getMessage());

            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return responseEntity;
    }

    @PostMapping("/add")
    public ResponseEntity<PatientRecord> addPathHistory(@RequestParam("patId") String patId, @RequestParam("note") String newNote) {
        List<ClinicalNote> clinicalNotes = new ArrayList<>();
        ResponseEntity<PatientRecord> responseEntity;
        PatientRecord patientRecord;

        log.info("Post request handling.../patHistory/add");

        if (patientRecordRepository.findByPatId(patId) != null) {
            patientRecord = patientRecordRepository.findByPatId(patId);
        } else {
            ClinicalNote clinicalNote = ClinicalNote.builder()
                    .date(LocalDate.now())
                    .note(newNote)
                    .build();

            patientRecord = PatientRecord.builder()
                    .patId(patId)
                    .clinicalNotes(clinicalNotes)
                    .build();
        }

        try {

            patientRecordRepository.save(patientRecord);

            responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(patientRecord);

            log.info("Saved patient-record: {}", patientRecord);
        } catch (RuntimeException ex) {
            log.error("Unable to saved Dr Note: {}", patientRecord);
            log.error(ex.getMessage());

            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return responseEntity;
    }
}
