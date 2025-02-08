package com.abernathyclinic.drnote.controller;

import com.abernathyclinic.drnote.model.DrNote;
import com.abernathyclinic.drnote.repository.DrNoteRepository;
import org.apache.coyote.Response;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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

    @Test
    void add_PathHistory() throws Exception {
        mockMvc.perform(post("http://localhost:8082/patHistory/add?patId=1&note=Patient: TestNone Practitioner's notes/recommendations: Patient states that they are 'feeling terrific' Weight at or below recommended level"))
                .andExpect(status().isCreated());
    }

    @Test
    void add_PathHistory_Existing_document() throws Exception {
        List<String> note = new ArrayList<>();
        note.add("Hello from the Dr note");

        DrNote drNote = DrNote.builder()
                .id("33")
                .notes(note)
                .build();

        when(drNoteRepository.findById(any(String.class))).thenReturn(Optional.of(drNote));

        mockMvc.perform(post("http://localhost:8082/patHistory/add?patId=33&note=Add more notes"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("33"))
                .andExpect(jsonPath("$.notes", hasSize(2)));
    }

    @Test
    void runtime_exception_add_pathHistory() throws Exception {
        when(drNoteRepository.save(any(DrNote.class))).thenThrow(new RuntimeException("Bad Request"));

        mockMvc.perform(post("http://localhost:8082/patHistory/add?patId=AA&note=#$$@%%%"))
                .andExpect(status().isInternalServerError());
    }

}