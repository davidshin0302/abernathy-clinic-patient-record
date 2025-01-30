package com.abernathyclinic.drnote.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collation = "drNotes")
public class DrNote {
    private Long id;
    private String note;
}
