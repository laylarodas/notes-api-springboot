package dev.layla.notesapi.note;

import dev.layla.notesapi.note.dto.CreateNoteRequest;
import dev.layla.notesapi.note.mapper.NoteMapper;
import dev.layla.notesapi.user.User;
import dev.layla.notesapi.user.UserRepository;
import dev.layla.notesapi.user.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NoteServiceTest {

    private NoteRepository noteRepository;
    private UserRepository userRepository;
    private NoteMapper noteMapper;

    private NoteService noteService;

    @BeforeEach
    void setUp() {
        noteRepository = mock(NoteRepository.class);
        userRepository = mock(UserRepository.class);
        noteMapper = new NoteMapper();
        noteService = new NoteService(noteRepository, noteMapper, userRepository); 
        // 👆 Ajusta el orden si tu constructor está diferente
    }

    @Test
    void create_shouldCreateNote_whenUserExists() {
        // Arrange
        CreateNoteRequest req = new CreateNoteRequest();
        req.setTitle("Test note");
        req.setContent("Hello");
        req.setUserId(1L);

        User owner = new User("Layla", "layla@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        // Simulamos el guardado: devolvemos una Note "guardada"
        Note saved = new Note("Test note", "Hello", owner);
        when(noteRepository.save(any(Note.class))).thenReturn(saved);

        // Act
        var res = noteService.create(req);

        // Assert
        assertEquals("Test note", res.getTitle());
        assertEquals("Hello", res.getContent());
        assertFalse(res.isArchived());

        verify(userRepository).findById(1L);
        verify(noteRepository).save(any(Note.class));
    }

    @Test
    void create_shouldThrowUserNotFound_whenUserDoesNotExist() {
        // Arrange
        CreateNoteRequest req = new CreateNoteRequest();
        req.setTitle("Test note");
        req.setContent("Hello");
        req.setUserId(999L);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(UserNotFoundException.class, () -> noteService.create(req));

        verify(userRepository).findById(999L);
        verify(noteRepository, never()).save(any());
    }
}

