package dev.layla.notesapi.note;

import dev.layla.notesapi.note.dto.CreateNoteRequest;
import dev.layla.notesapi.note.dto.NoteResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponse create(@Valid @RequestBody CreateNoteRequest request) {
        return noteService.create(request);
    }

    @GetMapping
    public List<NoteResponse> getAll() {
        return noteService.getAll();
    }

    @GetMapping("/{id}")
    public NoteResponse getById(@PathVariable Long id) {
        return noteService.getById(id);
    }

}
