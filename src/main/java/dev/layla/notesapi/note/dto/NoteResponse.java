package dev.layla.notesapi.note.dto;

import java.time.LocalDateTime;

public class NoteResponse {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private boolean archived;

    public NoteResponse(Long id, String title, String content, LocalDateTime createdAt, boolean archived) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.archived = archived;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isArchived() { return archived; }
}
