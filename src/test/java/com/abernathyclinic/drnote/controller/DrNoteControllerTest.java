package com.abernathyclinic.drnote.controller;

import com.abernathyclinic.drnote.repository.DrNoteRepository;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(DrNoteController.class)
class DrNoteControllerTest {
    @MockitoBean
    DrNoteRepository drNoteRepository;
    @InjectMocks
    DrNoteController drNoteController;
    @Autowired
    MockMvc mockMvc;

}