package dev.layla.notesapi.user;

import dev.layla.notesapi.user.dto.CreateUserRequest;
import dev.layla.notesapi.user.dto.UpdateUserRequest;
import dev.layla.notesapi.user.dto.UserResponse;
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

    public UserController(UserService userService) {
        this.userService = userService;
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
}

