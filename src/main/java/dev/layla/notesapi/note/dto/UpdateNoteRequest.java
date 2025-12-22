package dev.layla.notesapi.note.dto;

import jakarta.validation.constraints.Size;

public class UpdateNoteRequest {

    @Size(max = 200, message = "title must be at most 200 characters")
    private String title;

    private String content;

    private Boolean archived;

    public UpdateNoteRequest() {}

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public Boolean getArchived() { return archived; }

    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setArchived(Boolean archived) { this.archived = archived; }
}
