package dev.layla.notesapi.note;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {
    // Más adelante agregaré métodos personalizados
}
