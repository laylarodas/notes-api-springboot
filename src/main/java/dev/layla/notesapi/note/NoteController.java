package dev.layla.notesapi.note;

import dev.layla.notesapi.note.dto.CreateNoteRequest;
import dev.layla.notesapi.note.dto.NoteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import dev.layla.notesapi.note.dto.UpdateNoteRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/notes")
@Tag(name = "Notes", description = "Gestión de notas")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear nota", description = "Crea una nueva nota")
    public NoteResponse create(@Valid @RequestBody CreateNoteRequest request) {
        return noteService.create(request);
    }

    /**
     * Endpoint de búsqueda - DEBE ir ANTES de /{id} para evitar conflicto de rutas.
     * Spring podría interpretar "search" como un ID si el orden fuera diferente.
     */
    @GetMapping("/search")
    @Operation(summary = "Buscar notas", description = "Busca notas por título o contenido (case-insensitive)")
    public Page<NoteResponse> search(
            @Parameter(description = "Texto a buscar en título o contenido")
            @RequestParam String query,
            @Parameter(description = "Filtrar por ID de usuario (opcional)")
            @RequestParam(required = false) Long userId,
            @PageableDefault(size = 10) Pageable pageable) {
        return noteService.search(query, userId, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener nota", description = "Obtiene una nota por su ID")
    public NoteResponse getById(@PathVariable Long id) {
        return noteService.getById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar nota", description = "Actualiza una nota existente")
    public NoteResponse update(@PathVariable Long id, @Valid @RequestBody UpdateNoteRequest request) {
        return noteService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar nota", description = "Elimina una nota por su ID")
    public void delete(@PathVariable Long id) {
        noteService.delete(id);
    }

    @GetMapping
    @Operation(summary = "Listar notas", description = "Obtiene todas las notas con filtros opcionales")
    public Page<NoteResponse> getAll(
            @Parameter(description = "Filtrar por ID de usuario")
            @RequestParam(required = false) Long userId,
            @Parameter(description = "Filtrar por estado de archivo")
            @RequestParam(required = false) Boolean archived,
            @PageableDefault(size = 10) Pageable pageable) {
        return noteService.getAll(userId, archived, pageable);
    }

}
