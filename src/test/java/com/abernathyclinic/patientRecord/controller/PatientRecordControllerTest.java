package com.abernathyclinic.patientRecord.controller;

import com.abernathyclinic.patientRecord.model.PatientRecord;
import com.abernathyclinic.patientRecord.repository.PatientRecordRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PatientRecordController.class)
class PatientRecordControllerTest {
    @MockitoBean
    PatientRecordRepository patientRecordRepository;
    @InjectMocks
    PatientRecordController doctorNoteController;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    PatientRecord patientRecord;
    List<PatientRecord> patientRecordList;

    @BeforeEach
    void setUP() throws IOException {
        List<String> note = new ArrayList<>();
        note.add("Hello from the Dr note");

        patientRecord = PatientRecord.builder()
                .patId("33")
//                .clinicalNotes(note)
                .build();

        objectMapper = new ObjectMapper();
        patientRecordList = objectMapper.readValue(new File("src/test/java/com/abernathyclinic/patientRecord/resource/testPatientRecords.json"), new TypeReference<List<PatientRecord>>() {
        });
    }

    @Test
    void add_PathHistory() throws Exception {
        mockMvc.perform(post("http://localhost:8082/patHistory/add?patId=1&note=Patient: TestNone Practitioner's notes/recommendations: Patient states that they are 'feeling terrific' Weight at or below recommended level"))
                .andExpect(status().isCreated());
    }

    @Test
    void add_PathHistory_Existing_document() throws Exception {
        when(patientRecordRepository.findByPatId(any(String.class))).thenReturn((patientRecord));

        mockMvc.perform(post("http://localhost:8082/patHistory/add?patId=33&note=Add more notes"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.patId").value("33"))
                .andExpect(jsonPath("$.notes", hasSize(2)));
    }

    @Test
    void runtime_exception_add_pathHistory() throws Exception {
        when(patientRecordRepository.save(any(PatientRecord.class))).thenThrow(new RuntimeException("Bad Request"));

        mockMvc.perform(post("http://localhost:8082/patHistory/add?patId=AA&note=#$$@%%%"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void view_patHistory() throws Exception {
        when(patientRecordRepository.findByPatId(any(String.class))).thenReturn(patientRecord);

        mockMvc.perform(get("http://localhost:8082/patHistory/get?patId=33"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patId").value(33));
    }

    @Test
    void view_patHistory_not_found() throws Exception {
        when(patientRecordRepository.findByPatId(any(String.class))).thenReturn(null);

        mockMvc.perform(get("http://localhost:8082/patHistory/get?patId=99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void view_patHistory_exception() throws Exception {
        when(patientRecordRepository.findByPatId(any(String.class))).thenThrow(new RuntimeException("Exception occurred"));

        mockMvc.perform(get("http://localhost:8082/patHistory/get?patId=###$$"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void get_doctorNotes() throws Exception {
        when(patientRecordRepository.findAll()).thenReturn(patientRecordList);

        mockMvc.perform(get("http://localhost:8082/patHistory/get/doctornotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctorNotes", hasSize(9)));
    }

    @Test
    void get_get_doctorNotes_exception() throws Exception {
        when(patientRecordRepository.findAll()).thenThrow(new RuntimeException("Error while running the applications"));

        mockMvc.perform(get("http://localhost:8082/patHistory/get/doctornotes"))
                .andExpect(status().isInternalServerError());
    }
}