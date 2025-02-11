package com.abernathyclinic.drnote.repository;

import com.abernathyclinic.drnote.model.DoctorNote;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DoctorNoteRepository extends MongoRepository<DoctorNote, String> {
    DoctorNote findByPatId(String patId);
}
