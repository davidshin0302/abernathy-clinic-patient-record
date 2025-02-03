package com.abernathyclinic.drnote.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collation = "drNotes")
public class DrNote {
    private Long id;
    private String note;
}
