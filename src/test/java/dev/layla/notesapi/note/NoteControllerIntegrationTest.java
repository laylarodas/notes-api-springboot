package dev.layla.notesapi.note;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.layla.notesapi.auth.JwtService;
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
    @Autowired
    JwtService jwtService;

    private User testUser;
    private String jwtToken;

    @BeforeEach
    void setup() {
        noteRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User("Layla", "layla@example.com", "password123");
        testUser = userRepository.save(testUser);
        
        // Generar token JWT para el usuario de prueba
        jwtToken = jwtService.generateToken(testUser);
    }

    @Test
    void postNotes_shouldReturn201_andCreatedNote() throws Exception {
        CreateNoteRequest req = new CreateNoteRequest("My first note", "Hello Spring Boot");

        mockMvc.perform(post("/notes")
                .header("Authorization", "Bearer " + jwtToken)
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
        CreateNoteRequest req = new CreateNoteRequest(null, "No title");

        mockMvc.perform(post("/notes")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postNotes_shouldReturn401_whenNoToken() throws Exception {
        CreateNoteRequest req = new CreateNoteRequest("My note", "Content");

        mockMvc.perform(post("/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getNoteById_shouldReturn200_whenExists() throws Exception {
        Note saved = noteRepository.save(new Note("Saved note", "From DB", testUser));

        mockMvc.perform(get("/notes/{id}", saved.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.title").value("Saved note"));
    }

    @Test
    void getNoteById_shouldReturn404_whenNotExists() throws Exception {
        mockMvc.perform(get("/notes/{id}", 999999L)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("was not found")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void getNoteById_shouldReturn403_whenNoteOwnedByOtherUser() throws Exception {
        // Crear otro usuario y su nota
        User otherUser = userRepository.save(new User("Other", "other@example.com", "password"));
        Note otherNote = noteRepository.save(new Note("Other's note", "Not yours", otherUser));

        // Intentar acceder con el token del primer usuario
        mockMvc.perform(get("/notes/{id}", otherNote.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void putNote_shouldUpdateFields_whenExists() throws Exception {
        Note saved = noteRepository.save(new Note("Old title", "Old content", testUser));

        UpdateNoteRequest req = new UpdateNoteRequest("New title", "New content", true);

        mockMvc.perform(put("/notes/{id}", saved.getId())
                .header("Authorization", "Bearer " + jwtToken)
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
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("was not found")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void deleteNote_shouldReturn204_andThenNoteIsGone() throws Exception {
        Note saved = noteRepository.save(new Note("To delete", "Bye", testUser));

        mockMvc.perform(delete("/notes/{id}", saved.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/notes/{id}", saved.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteNote_shouldReturn404_whenNotExists() throws Exception {
        mockMvc.perform(delete("/notes/{id}", 999999L)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("was not found")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }
}
