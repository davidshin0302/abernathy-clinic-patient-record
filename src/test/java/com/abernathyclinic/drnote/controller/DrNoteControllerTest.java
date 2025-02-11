package com.abernathyclinic.drnote.controller;

import com.abernathyclinic.drnote.model.DrNote;
import com.abernathyclinic.drnote.repository.DrNoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DrNoteController.class)
class DrNoteControllerTest {
    @MockitoBean
    DrNoteRepository drNoteRepository;
    @InjectMocks
    DrNoteController drNoteController;
    @Autowired
    MockMvc mockMvc;

    DrNote drNote;

    @BeforeEach
    void setUP() {
        List<String> note = new ArrayList<>();
        note.add("Hello from the Dr note");

        drNote = DrNote.builder()
                .patId("33")
                .notes(note)
                .build();
    }

    @Test
    void add_PathHistory() throws Exception {
        mockMvc.perform(post("http://localhost:8082/patHistory/add?patId=1&note=Patient: TestNone Practitioner's notes/recommendations: Patient states that they are 'feeling terrific' Weight at or below recommended level"))
                .andExpect(status().isCreated());
    }

    @Test
    void add_PathHistory_Existing_document() throws Exception {
        when(drNoteRepository.findById(any(String.class))).thenReturn(Optional.of(drNote));

        mockMvc.perform(post("http://localhost:8082/patHistory/add?patId=33&note=Add more notes"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.patId").value("33"))
                .andExpect(jsonPath("$.notes", hasSize(2)));
    }

    @Test
    void runtime_exception_add_pathHistory() throws Exception {
        when(drNoteRepository.save(any(DrNote.class))).thenThrow(new RuntimeException("Bad Request"));

        mockMvc.perform(post("http://localhost:8082/patHistory/add?patId=AA&note=#$$@%%%"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void view_patHistory() throws Exception {
        when(drNoteRepository.findByPatId(any(String.class))).thenReturn(drNote);

        mockMvc.perform(get("http://localhost:8082/patHistory/get?patId=33"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patId").value(33));
    }

    @Test
    void view_patHistory_not_found() throws Exception {
        when(drNoteRepository.findByPatId(any(String.class))).thenReturn(null);

        mockMvc.perform(get("http://localhost:8082/patHistory/get?patId=99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void view_patHistory_exception() throws Exception {
        when(drNoteRepository.findByPatId(any(String.class))).thenThrow(new RuntimeException("Exception occurred"));

        mockMvc.perform(get("http://localhost:8082/patHistory/get?patId=###$$"))
                .andExpect(status().isInternalServerError());
    }
}