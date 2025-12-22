package dev.layla.notesapi.note;

import dev.layla.notesapi.note.dto.CreateNoteRequest;
import dev.layla.notesapi.note.dto.NoteResponse;
import org.springframework.stereotype.Service;


import dev.layla.notesapi.note.exception.NoteNotFoundException;
import dev.layla.notesapi.note.dto.UpdateNoteRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public NoteResponse create(CreateNoteRequest request) {
        Note note = new Note(request.getTitle(), request.getContent());
        Note saved = noteRepository.save(note);

        return new NoteResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getCreatedAt(),
                saved.isArchived());
    }

    public List<NoteResponse> getAll() {
        return noteRepository.findAll()
                .stream()
                .map(note -> new NoteResponse(
                        note.getId(),
                        note.getTitle(),
                        note.getContent(),
                        note.getCreatedAt(),
                        note.isArchived()))
                .toList();
    }

    public NoteResponse getById(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));

        return new NoteResponse(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getCreatedAt(),
                note.isArchived());
    }

    @Transactional
    public NoteResponse update(Long id, UpdateNoteRequest request) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            note.setTitle(request.getTitle());
        }

        if (request.getContent() != null) {
            note.setContent(request.getContent());
        }

        if (request.getArchived() != null) {
            note.setArchived(request.getArchived());
        }

        Note saved = noteRepository.save(note);

        return new NoteResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getCreatedAt(),
                saved.isArchived());
    }

    public void delete(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));
        noteRepository.delete(note);
    }
}
