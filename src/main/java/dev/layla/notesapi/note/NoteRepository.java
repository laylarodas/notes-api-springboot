package dev.layla.notesapi.note;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoteRepository extends JpaRepository<Note, Long> {

    Page<Note> findAllByOwnerId(Long ownerId, Pageable pageable);

    Page<Note> findAllByArchived(boolean archived, Pageable pageable);

    Page<Note> findAllByOwnerIdAndArchived(Long ownerId, boolean archived, Pageable pageable);

    boolean existsByIdAndOwnerId(Long id, Long ownerId);

    /**
     * Búsqueda de notas por título o contenido (case-insensitive).
     * Usa LOWER() para que la búsqueda no distinga mayúsculas/minúsculas.
     */
    @Query("SELECT n FROM Note n WHERE " +
           "LOWER(n.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(n.content) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Note> searchByTitleOrContent(@Param("query") String query, Pageable pageable);

    /**
     * Búsqueda de notas de un usuario específico.
     */
    @Query("SELECT n FROM Note n WHERE n.owner.id = :userId AND (" +
           "LOWER(n.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(n.content) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Note> searchByUserAndTitleOrContent(
            @Param("userId") Long userId,
            @Param("query") String query,
            Pageable pageable
    );
}
