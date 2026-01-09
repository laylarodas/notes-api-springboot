package dev.layla.notesapi.note;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoteRepository extends JpaRepository<Note, Long> {
    // Más adelante agregaré métodos personalizados
    Page<Note> findAllByOwnerId(Long ownerId, Pageable pageable);

    Page<Note> findAllByArchived(boolean archived, Pageable pageable);

    Page<Note> findAllByOwnerIdAndArchived(Long ownerId, boolean archived, Pageable pageable);

    boolean existsByIdAndOwnerId(Long id, Long ownerId);
}
