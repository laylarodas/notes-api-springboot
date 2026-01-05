package dev.layla.notesapi.user.mapper;

import dev.layla.notesapi.user.User;
import dev.layla.notesapi.user.dto.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getNotes() != null ? user.getNotes().size() : 0
        );
    }
}

