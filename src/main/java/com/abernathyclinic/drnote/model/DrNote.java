package com.abernathyclinic.drnote.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "drNotes")
public class DrNote {
    @Id
    private String id;
    private String patId;
    private List<String> notes;

    public void addNote(String note){
        this.notes.add(note);
    }
}
