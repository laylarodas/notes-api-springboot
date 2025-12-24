package dev.layla.notesapi.note.mapper;

import dev.layla.notesapi.note.Note;
import dev.layla.notesapi.note.dto.NoteResponse;
import org.springframework.stereotype.Component;

@Component
public class NoteMapper {

    public NoteResponse toResponse(Note note) {
        return new NoteResponse(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getCreatedAt(),
                note.isArchived()
        );
    }
}
