package com.abernathyclinic.patientRecord.repository;

import com.abernathyclinic.patientRecord.model.PatientRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PatientRecordRepository extends MongoRepository<PatientRecord, String> {
    PatientRecord findByPatId(String patId);
}
