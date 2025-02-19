package com.abernathyclinic.patientRecord.model;

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
@Document(collection = "patient-records")
public class PatientRecord {
    @Id
    private String id;
    private String patId;
    private List<ClinicalNote> clinicalNotes;

    public void addClinicalNote(ClinicalNote clinicalNote) {
        this.clinicalNotes.add(clinicalNote);
    }

    public void updateClinicalNote(int index, ClinicalNote clinicalNote) {
        this.clinicalNotes.set(index, clinicalNote);
    }

}
