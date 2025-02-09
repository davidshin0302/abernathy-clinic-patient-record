package com.abernathyclinic.drnote.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DrNotesList {
    private List<DrNote> drNoteList;
}
