package com.abernathyclinic.drnote.repository;

import com.abernathyclinic.drnote.model.DrNote;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface drNoteRepository extends MongoRepository<DrNote, String> {
}
