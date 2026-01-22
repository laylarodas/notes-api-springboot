package dev.layla.notesapi.note;

import dev.layla.notesapi.note.dto.CreateNoteRequest;
import dev.layla.notesapi.note.dto.NoteResponse;
import dev.layla.notesapi.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import dev.layla.notesapi.note.dto.UpdateNoteRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

/**
 * Controller para gestión de notas.
 * Todos los endpoints requieren autenticación JWT.
 * Las notas solo son accesibles por su propietario.
 */
@RestController
@RequestMapping("/notes")
@Tag(name = "Notes", description = "Gestión de notas del usuario autenticado")
@SecurityRequirement(name = "bearerAuth")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * Crea una nueva nota para el usuario autenticado.
     * El userId se obtiene automáticamente del token JWT.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear nota", description = "Crea una nueva nota para el usuario autenticado")
    public NoteResponse create(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateNoteRequest request) {
        return noteService.createForUser(currentUser.getId(), request);
    }

    /**
     * Obtiene todas las notas del usuario autenticado.
     */
    @GetMapping
    @Operation(summary = "Listar mis notas", description = "Obtiene todas las notas del usuario autenticado")
    public Page<NoteResponse> getMyNotes(
            @AuthenticationPrincipal User currentUser,
            @Parameter(description = "Filtrar por estado de archivo")
            @RequestParam(required = false) Boolean archived,
            @PageableDefault(size = 10) Pageable pageable) {
        return noteService.getAll(currentUser.getId(), archived, pageable);
    }

    /**
     * Busca notas del usuario autenticado por título o contenido.
     */
    @GetMapping("/search")
    @Operation(summary = "Buscar mis notas", description = "Busca en las notas del usuario autenticado por título o contenido")
    public Page<NoteResponse> searchMyNotes(
            @AuthenticationPrincipal User currentUser,
            @Parameter(description = "Texto a buscar en título o contenido")
            @RequestParam String query,
            @PageableDefault(size = 10) Pageable pageable) {
        return noteService.search(query, currentUser.getId(), pageable);
    }

    /**
     * Obtiene una nota por ID (solo si pertenece al usuario autenticado).
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener nota", description = "Obtiene una nota por su ID (debe ser del usuario autenticado)")
    public NoteResponse getById(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        return noteService.getByIdForUser(currentUser.getId(), id);
    }

    /**
     * Actualiza una nota (solo si pertenece al usuario autenticado).
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar nota", description = "Actualiza una nota existente (debe ser del usuario autenticado)")
    public NoteResponse update(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id,
            @Valid @RequestBody UpdateNoteRequest request) {
        return noteService.updateForUser(currentUser.getId(), id, request);
    }

    /**
     * Elimina una nota (solo si pertenece al usuario autenticado).
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar nota", description = "Elimina una nota por su ID (debe ser del usuario autenticado)")
    public void delete(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        noteService.deleteForUser(currentUser.getId(), id);
    }
}
