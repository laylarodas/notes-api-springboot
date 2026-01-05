package dev.layla.notesapi.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UpdateUserRequest {

    @Size(max = 100, message = "name must be at most 100 characters")
    private String name;

    @Email(message = "email must be a valid email address")
    @Size(max = 150, message = "email must be at most 150 characters")
    private String email;

    public UpdateUserRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

