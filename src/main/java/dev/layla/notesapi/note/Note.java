package dev.layla.notesapi.note;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import dev.layla.notesapi.user.User;

@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean archived = false;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    // Constructor vacío requerido por JPA
    protected Note() {
    }

    // Constructor útil para crear notas desde el código
    public Note(String title, String content, User owner) {
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.archived = false;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User getOwner() {
        return owner;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
