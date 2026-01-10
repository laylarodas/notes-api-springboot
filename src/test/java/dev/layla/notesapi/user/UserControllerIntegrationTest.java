package dev.layla.notesapi.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.layla.notesapi.note.Note;
import dev.layla.notesapi.note.NoteRepository;
import dev.layla.notesapi.user.dto.CreateUserRequest;
import dev.layla.notesapi.user.dto.UpdateUserRequest;

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
class UserControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    NoteRepository noteRepository;

    @BeforeEach
    void setup() {
        // Limpiamos en orden correcto por las foreign keys
        noteRepository.deleteAll();
        userRepository.deleteAll();
    }

    // ========== POST /users ==========

    @Test
    void postUsers_shouldReturn201_andCreatedUser() throws Exception {
        CreateUserRequest req = new CreateUserRequest("Layla", "layla@example.com", "password123");

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name").value("Layla"))
                .andExpect(jsonPath("$.email").value("layla@example.com"))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.notesCount").value(0));
    }

    @Test
    void postUsers_shouldReturn400_whenNameMissing() throws Exception {
        // name es null
        CreateUserRequest req = new CreateUserRequest(null, "test@example.com", "password123");

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.errors[?(@.field == 'name')]", hasSize(1)));
    }

    @Test
    void postUsers_shouldReturn400_whenEmailInvalid() throws Exception {
        CreateUserRequest req = new CreateUserRequest("Test", "not-an-email", "password123");

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'email')]", hasSize(1)));
    }

    @Test
    void postUsers_shouldReturn400_whenEmailMissing() throws Exception {
        CreateUserRequest req = new CreateUserRequest("Test", null, "password123");

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'email')]", hasSize(1)));
    }

    // ========== GET /users ==========

    @Test
    void getUsers_shouldReturnPageOfUsers() throws Exception {
        // Arrange: crear algunos usuarios
        userRepository.save(new User("User1", "user1@example.com", "password123"));
        userRepository.save(new User("User2", "user2@example.com", "password123"));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name", notNullValue()))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void getUsers_shouldReturnEmptyPage_whenNoUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    // ========== GET /users/{id} ==========

    @Test
    void getUserById_shouldReturn200_whenExists() throws Exception {
        User saved = userRepository.save(new User("Layla", "layla@example.com", "password123"));

        mockMvc.perform(get("/users/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Layla"))
                .andExpect(jsonPath("$.email").value("layla@example.com"));
    }

    @Test
    void getUserById_shouldReturn404_whenNotExists() throws Exception {
        mockMvc.perform(get("/users/{id}", 999999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("was not found")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    // ========== PUT /users/{id} ==========

    @Test
    void putUser_shouldUpdateFields_whenExists() throws Exception {
        User saved = userRepository.save(new User("Old Name", "old@example.com", "password123"));

        UpdateUserRequest req = new UpdateUserRequest("New Name", "new@example.com");

        mockMvc.perform(put("/users/{id}", saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    void putUser_shouldUpdateOnlyName_whenEmailNotProvided() throws Exception {
        User saved = userRepository.save(new User("Old Name", "keep@example.com", "password123"));

        UpdateUserRequest req = new UpdateUserRequest("New Name", null);

        mockMvc.perform(put("/users/{id}", saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.email").value("keep@example.com")); // Sin cambios
    }

    @Test
    void putUser_shouldReturn404_whenNotExists() throws Exception {
        UpdateUserRequest req = new UpdateUserRequest("Doesn't matter", null);

        mockMvc.perform(put("/users/{id}", 999999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("was not found")));
    }

    @Test
    void putUser_shouldReturn400_whenEmailInvalid() throws Exception {
        User saved = userRepository.save(new User("Test", "test@example.com", "password123"));

        UpdateUserRequest req = new UpdateUserRequest(null, "invalid-email");

        mockMvc.perform(put("/users/{id}", saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'email')]", hasSize(1)));
    }

    // ========== DELETE /users/{id} ==========

    @Test
    void deleteUser_shouldReturn204_andThenUserIsGone() throws Exception {
        User saved = userRepository.save(new User("To Delete", "delete@example.com", "password123"));

        mockMvc.perform(delete("/users/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        // Verificar que ya no existe
        mockMvc.perform(get("/users/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_shouldReturn404_whenNotExists() throws Exception {
        mockMvc.perform(delete("/users/{id}", 999999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("was not found")));
    }

    @Test
    void deleteUser_shouldAlsoDeleteUserNotes() throws Exception {
        // Crear usuario con notas
        User user = userRepository.save(new User("With Notes", "notes@example.com", "password123"));
        noteRepository.save(new Note("Note 1", "Content 1", user));
        noteRepository.save(new Note("Note 2", "Content 2", user));

        // Eliminar usuario
        mockMvc.perform(delete("/users/{id}", user.getId()))
                .andExpect(status().isNoContent());

        // Verificar que las notas también fueron eliminadas (orphanRemoval = true)
        mockMvc.perform(get("/notes?userId=" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    // ========== GET /users/{userId}/notes ==========

    @Test
    void getUserNotes_shouldReturnNotesForUser() throws Exception {
        User user = userRepository.save(new User("Note Owner", "owner@example.com", "password123"));
        noteRepository.save(new Note("My Note 1", "Content 1", user));
        noteRepository.save(new Note("My Note 2", "Content 2", user));

        mockMvc.perform(get("/users/{userId}/notes", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title", notNullValue()));
    }

    @Test
    void getUserNotes_shouldReturn404_whenUserNotExists() throws Exception {
        mockMvc.perform(get("/users/{userId}/notes", 999999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("was not found")));
    }
}
