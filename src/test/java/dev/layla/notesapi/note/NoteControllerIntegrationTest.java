package dev.layla.notesapi.note;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.layla.notesapi.note.dto.CreateNoteRequest;
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

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Autowired UserRepository userRepository;
    @Autowired NoteRepository noteRepository;

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
        CreateNoteRequest req = new CreateNoteRequest();
        req.setTitle("My first note");
        req.setContent("Hello Spring Boot");
        req.setUserId(userId);

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
        CreateNoteRequest req = new CreateNoteRequest();
        req.setContent("No title");
        req.setUserId(userId);

        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getNoteById_shouldReturn200_whenExists() throws Exception {
        // Arrange: creamos una nota via repo para testear GET
        Note saved = noteRepository.save(new Note("Saved note", "From DB", userRepository.findById(userId).orElseThrow()));

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
}
