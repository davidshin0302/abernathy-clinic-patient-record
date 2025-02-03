package com.abernathyclinic.drnote.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Document(collection = "drNotes")
public class DrNote {
    @Id
    private String id;
    private List<String> notes;

    public void addNote(String note){
        this.notes.add(note);
    }
}
