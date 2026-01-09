package dev.layla.notesapi.user;

import dev.layla.notesapi.user.dto.CreateUserRequest;
import dev.layla.notesapi.user.dto.UpdateUserRequest;
import dev.layla.notesapi.user.dto.UserResponse;
import dev.layla.notesapi.note.dto.CreateNoteRequest;
import dev.layla.notesapi.note.dto.NoteResponse;
import dev.layla.notesapi.note.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Gestión de usuarios")
public class UserController {

    private final UserService userService;
    private final NoteService noteService;

    public UserController(UserService userService, NoteService noteService) {
        this.userService = userService;
        this.noteService = noteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario")
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return userService.create(request);
    }

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Obtiene todos los usuarios con paginación")
    public Page<UserResponse> getAll(@PageableDefault(size = 10) Pageable pageable) {
        return userService.getAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario", description = "Obtiene un usuario por su ID")
    public UserResponse getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario")
    public UserResponse update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario y todas sus notas")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    
    @GetMapping("/{userId}/notes")
    public Page<NoteResponse> getUserNotes(
            @PathVariable Long userId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return noteService.getAllByUser(userId, pageable);
    }
    
    @PostMapping("/{userId}/notes")
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponse createNoteForUser(
            @PathVariable Long userId,
            @Valid @RequestBody CreateNoteRequest request
    ) {
        return noteService.createForUser(userId, request);
    }
}

