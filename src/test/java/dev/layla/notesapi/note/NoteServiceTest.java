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
    }

    @Test
    void createForUser_shouldCreateNote_whenUserExists() {
        // Arrange
        Long userId = 1L;
        CreateNoteRequest req = new CreateNoteRequest("Test note", "Hello");

        User owner = new User("Layla", "layla@example.com", "password123");

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));

        // Simulamos el guardado: devolvemos una Note "guardada"
        Note saved = new Note("Test note", "Hello", owner);
        when(noteRepository.save(any(Note.class))).thenReturn(saved);

        // Act
        var res = noteService.createForUser(userId, req);

        // Assert
        assertEquals("Test note", res.title());
        assertEquals("Hello", res.content());
        assertFalse(res.archived());

        verify(userRepository).findById(userId);
        verify(noteRepository).save(any(Note.class));
    }

    @Test
    void createForUser_shouldThrowUserNotFound_whenUserDoesNotExist() {
        // Arrange
        Long userId = 999L;
        CreateNoteRequest req = new CreateNoteRequest("Test note", "Hello");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(UserNotFoundException.class, () -> noteService.createForUser(userId, req));

        verify(userRepository).findById(userId);
        verify(noteRepository, never()).save(any());
    }
}
