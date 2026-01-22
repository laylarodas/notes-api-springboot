package dev.layla.notesapi.note;

import dev.layla.notesapi.note.dto.CreateNoteRequest;
import dev.layla.notesapi.note.dto.NoteResponse;
import org.springframework.stereotype.Service;

import dev.layla.notesapi.note.exception.NoteAccessDeniedException;
import dev.layla.notesapi.note.exception.NoteNotFoundException;
import dev.layla.notesapi.note.dto.UpdateNoteRequest;
import dev.layla.notesapi.note.mapper.NoteMapper;
import org.springframework.transaction.annotation.Transactional;
import dev.layla.notesapi.user.User;
import dev.layla.notesapi.user.UserRepository;
import dev.layla.notesapi.user.exception.UserNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;
    private final UserRepository userRepository;

    public NoteService(NoteRepository noteRepository, NoteMapper noteMapper, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.noteMapper = noteMapper;
        this.userRepository = userRepository;
    }

    /**
     * @deprecated Usar createForUser() que obtiene el userId del token JWT
     */
    @Deprecated
    public NoteResponse create(Long userId, CreateNoteRequest request) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Note note = new Note(request.title(), request.content(), owner);
        Note saved = noteRepository.save(note);

        return noteMapper.toResponse(saved);
    }

    public Page<NoteResponse> getAll(Long userId, Boolean archived, Pageable pageable) {

        Page<Note> page;

        if (userId != null && archived != null) {
            page = noteRepository.findAllByOwnerIdAndArchived(userId, archived, pageable);
        } else if (userId != null) {
            page = noteRepository.findAllByOwnerId(userId, pageable);
        } else if (archived != null) {
            page = noteRepository.findAllByArchived(archived, pageable);
        } else {
            page = noteRepository.findAll(pageable);
        }

        return page.map(noteMapper::toResponse);
    }

    public NoteResponse getById(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));

        return noteMapper.toResponse(note);
    }

    /**
     * Obtiene una nota por ID verificando que pertenezca al usuario.
     */
    public NoteResponse getByIdForUser(Long userId, Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException(noteId));

        if (!note.getOwner().getId().equals(userId)) {
            throw new NoteAccessDeniedException(noteId, userId);
        }

        return noteMapper.toResponse(note);
    }

    @Transactional
    public NoteResponse update(Long id, UpdateNoteRequest request) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));

        if (request.title() != null && !request.title().isBlank()) {
            note.setTitle(request.title());
        }

        if (request.content() != null) {
            note.setContent(request.content());
        }

        if (request.archived() != null) {
            note.setArchived(request.archived());
        }

        return noteMapper.toResponse(noteRepository.save(note));
    }

    public void delete(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));
        noteRepository.delete(note);
    }

    @Transactional
    public NoteResponse updateForUser(Long userId, Long noteId, UpdateNoteRequest request) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException(noteId));

        if (!note.getOwner().getId().equals(userId)) {
            throw new NoteAccessDeniedException(noteId, userId);
        }

        if (request.title() != null && !request.title().isBlank()) {
            note.setTitle(request.title());
        }

        if (request.content() != null) {
            note.setContent(request.content());
        }

        if (request.archived() != null) {
            note.setArchived(request.archived());
        }

        Note saved = noteRepository.save(note);

        return noteMapper.toResponse(saved);
    }

    public void deleteForUser(Long userId, Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException(noteId));

        if (!note.getOwner().getId().equals(userId)) {
            throw new NoteAccessDeniedException(noteId, userId);
        }
        noteRepository.delete(note);
    }

    public Page<NoteResponse> getAllByUser(Long userId, Pageable pageable) {
        // valida que el user exista (opcional pero pro)
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        return noteRepository.findAllByOwnerId(userId, pageable).map(noteMapper::toResponse);
    }

    public NoteResponse createForUser(Long userId, CreateNoteRequest request) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Note saved = noteRepository.save(new Note(request.title(), request.content(), owner));
        return noteMapper.toResponse(saved);
    }

    /**
     * Busca notas por título o contenido (case-insensitive).
     * Opcionalmente puede filtrar por userId.
     *
     * @param query  Texto a buscar en título o contenido
     * @param userId (Opcional) Filtrar por usuario
     * @param pageable Configuración de paginación
     * @return Página de notas que coinciden con la búsqueda
     */
    public Page<NoteResponse> search(String query, Long userId, Pageable pageable) {
        Page<Note> page;

        if (userId != null) {
            // Validar que el usuario existe
            userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
            page = noteRepository.searchByUserAndTitleOrContent(userId, query, pageable);
        } else {
            page = noteRepository.searchByTitleOrContent(query, pageable);
        }

        return page.map(noteMapper::toResponse);
    }
}
