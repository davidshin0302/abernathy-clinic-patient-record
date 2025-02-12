package com.abernathyclinic.drnote.controller;

import com.abernathyclinic.drnote.model.DoctorNote;
import com.abernathyclinic.drnote.repository.DoctorNoteRepository;
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

@WebMvcTest(DoctorNoteController.class)
class DoctorNoteControllerTest {
    @MockitoBean
    DoctorNoteRepository doctorNoteRepository;
    @InjectMocks
    DoctorNoteController doctorNoteController;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    DoctorNote doctorNote;
    List<DoctorNote> doctorNoteList;

    @BeforeEach
    void setUP() throws IOException {
        List<String> note = new ArrayList<>();
        note.add("Hello from the Dr note");

        doctorNote = DoctorNote.builder()
                .patId("33")
                .notes(note)
                .build();

        objectMapper = new ObjectMapper();
        doctorNoteList = objectMapper.readValue(new File("src/test/java/com/abernathyclinic/drnote/resource/testDrNotes.json"), new TypeReference<List<DoctorNote>>() {
        });
    }

    @Test
    void add_PathHistory() throws Exception {
        mockMvc.perform(post("http://localhost:8082/patHistory/add?patId=1&note=Patient: TestNone Practitioner's notes/recommendations: Patient states that they are 'feeling terrific' Weight at or below recommended level"))
                .andExpect(status().isCreated());
    }

    @Test
    void add_PathHistory_Existing_document() throws Exception {
        when(doctorNoteRepository.findByPatId(any(String.class))).thenReturn((doctorNote));

        mockMvc.perform(post("http://localhost:8082/patHistory/add?patId=33&note=Add more notes"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.patId").value("33"))
                .andExpect(jsonPath("$.notes", hasSize(2)));
    }

    @Test
    void runtime_exception_add_pathHistory() throws Exception {
        when(doctorNoteRepository.save(any(DoctorNote.class))).thenThrow(new RuntimeException("Bad Request"));

        mockMvc.perform(post("http://localhost:8082/patHistory/add?patId=AA&note=#$$@%%%"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void view_patHistory() throws Exception {
        when(doctorNoteRepository.findByPatId(any(String.class))).thenReturn(doctorNote);

        mockMvc.perform(get("http://localhost:8082/patHistory/get?patId=33"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patId").value(33));
    }

    @Test
    void view_patHistory_not_found() throws Exception {
        when(doctorNoteRepository.findByPatId(any(String.class))).thenReturn(null);

        mockMvc.perform(get("http://localhost:8082/patHistory/get?patId=99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void view_patHistory_exception() throws Exception {
        when(doctorNoteRepository.findByPatId(any(String.class))).thenThrow(new RuntimeException("Exception occurred"));

        mockMvc.perform(get("http://localhost:8082/patHistory/get?patId=###$$"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void get_doctorNotes() throws Exception {
        when(doctorNoteRepository.findAll()).thenReturn(doctorNoteList);

        mockMvc.perform(get("http://localhost:8082/patHistory/get/doctornotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctorNotes", hasSize(9)));
    }

    @Test
    void get_get_doctorNotes_exception() throws Exception {
        when(doctorNoteRepository.findAll()).thenThrow(new RuntimeException("Error while running the applications"));

        mockMvc.perform(get("http://localhost:8082/patHistory/get/doctornotes"))
                .andExpect(status().isInternalServerError());
    }
}