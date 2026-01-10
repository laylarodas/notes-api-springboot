package dev.layla.notesapi.note;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.layla.notesapi.note.dto.CreateNoteRequest;
import dev.layla.notesapi.note.dto.UpdateNoteRequest;
import dev.layla.notesapi.user.User;
import dev.layla.notesapi.user.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class NoteControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;
    @Autowired
    NoteRepository noteRepository;

    private Long userId;

    @BeforeEach
    void setup() {
        noteRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User("Layla", "layla@example.com");
        user = userRepository.save(user);
        userId = user.getId();
    }

    @Test
    void postNotes_shouldReturn201_andCreatedNote() throws Exception {
        // Usando constructor de record
        CreateNoteRequest req = new CreateNoteRequest("My first note", "Hello Spring Boot", userId);

        mockMvc.perform(post("/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title").value("My first note"))
                .andExpect(jsonPath("$.content").value("Hello Spring Boot"))
                .andExpect(jsonPath("$.archived").value(false))
                .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    void postNotes_shouldReturn400_whenTitleMissing() throws Exception {
        // title es null, debería fallar validación
        CreateNoteRequest req = new CreateNoteRequest(null, "No title", userId);

        mockMvc.perform(post("/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getNoteById_shouldReturn200_whenExists() throws Exception {
        // Arrange: creamos una nota via repo para testear GET
        Note saved = noteRepository
                .save(new Note("Saved note", "From DB", userRepository.findById(userId).orElseThrow()));

        mockMvc.perform(get("/notes/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.title").value("Saved note"));
    }

    @Test
    void getNoteById_shouldReturn404_whenNotExists() throws Exception {
        mockMvc.perform(get("/notes/{id}", 999999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("was not found")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void putNote_shouldUpdateFields_whenExists() throws Exception {
        User owner = userRepository.findById(userId).orElseThrow();
        Note saved = noteRepository.save(new Note("Old title", "Old content", owner));

        // Usando constructor de record
        UpdateNoteRequest req = new UpdateNoteRequest("New title", "New content", true);

        mockMvc.perform(put("/notes/{id}", saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.title").value("New title"))
                .andExpect(jsonPath("$.content").value("New content"))
                .andExpect(jsonPath("$.archived").value(true))
                .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    void putNote_shouldReturn404_whenNotExists() throws Exception {
        UpdateNoteRequest req = new UpdateNoteRequest("Doesn't matter", null, null);

        mockMvc.perform(put("/notes/{id}", 999999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("was not found")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void deleteNote_shouldReturn204_andThenNoteIsGone() throws Exception {
        User owner = userRepository.findById(userId).orElseThrow();
        Note saved = noteRepository.save(new Note("To delete", "Bye", owner));

        mockMvc.perform(delete("/notes/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/notes/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteNote_shouldReturn404_whenNotExists() throws Exception {
        mockMvc.perform(delete("/notes/{id}", 999999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("was not found")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }
}
