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

/**
 * REST controller for managing patient records.
 * This class handles requests related to viewing, retrieving, and adding patient history.
 */
@Slf4j
@Controller
@RequestMapping("/patHistory")
@CrossOrigin(origins = "http://localhost:3000")
public class PatientRecordController {
    @Autowired
    private PatientRecordRepository patientRecordRepository;

    /**
     * Retrieves a patient's record based on their patient ID.
     *
     * @param patId The patient ID to search for.
     * @return A ResponseEntity containing the PatientRecord if found, or a 404 Not Found status if not.  Returns a 500 Internal Server Error if a database error occurs.
     */
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

    /**
     * Retrieves all patient records.
     *
     * @return A ResponseEntity containing a list of all PatientRecords, or a 500 Internal Server Error if a database error occurs.
     */
    @GetMapping("/get/patient-records")
    public ResponseEntity<PatientRecords> getPatientRecords() {
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

    /**
     * Adds a new clinical note to a patient's record.  Creates a new patient record if one doesn't already exist for the given patId.
     *
     * @param patId   The patient ID to add the note to.
     * @param newNote The new clinical note to add.
     * @return A ResponseEntity containing the updated PatientRecord, or a 500 Internal Server Error if a database error occurs. Returns a 201 Created status upon successful creation/update.
     */
    @PostMapping("/add")
    public ResponseEntity<PatientRecord> addPathHistory(@RequestParam("patId") String patId, @RequestParam("note") String newNote) {
        List<ClinicalNote> clinicalNotes = new ArrayList<>();
        ResponseEntity<PatientRecord> responseEntity;
        PatientRecord patientRecord;

        log.info("Post request handling.../patHistory/add");

        if (patientRecordRepository.findByPatId(patId) != null) {
            patientRecord = patientRecordRepository.findByPatId(patId);
        } else {
            patientRecord = PatientRecord.builder()
                    .patId(patId)
                    .clinicalNotes(clinicalNotes)
                    .build();
        }

        try {
            ClinicalNote clinicalNote = ClinicalNote.builder()
                    .date(LocalDate.now())
                    .note(newNote)
                    .build();

            patientRecord.addClinicalNote(clinicalNote);
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

    @PutMapping("/update/{patId}")
    public ResponseEntity<PatientRecord> updatePatientRecord(@PathVariable String patId, @RequestParam String updatedRecord, @RequestParam String recordDate) {
        ResponseEntity<PatientRecord> responseEntity;
        PatientRecord patientRecord;

        try {
            patientRecord = patientRecordRepository.findByPatId(patId);
            ClinicalNote clinicalNote = patientRecord.getClinicalNotes().stream().filter(note -> note.getDate().toString().equalsIgnoreCase(recordDate)).findFirst().orElse(null);


            if(clinicalNote != null){
                int index = patientRecord.getClinicalNotes().indexOf(clinicalNote);

                clinicalNote.setNote(updatedRecord);
//                patientRecord.updateClinicalNote(index, clinicalNote);
            }
        } catch (RuntimeException ex) {
            log.error("Unable to update patient record, patId:{}", patId);
            log.error(ex.getMessage());

            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
