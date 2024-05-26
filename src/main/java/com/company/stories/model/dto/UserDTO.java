package com.company.stories.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UserDTO", description = "Representation of user entity")
public class UserDTO {

    @Schema(name = "userId", description = "Unique user id", example = "1")
    @Nullable
    Long userId;

    @Schema(name = "name", description = "user name", example = "John")
    String name;

    @Schema(name = "surname", description = "user surname", example = "Smith")
    String surname;

    @Schema(name = "email", description = "Unique user email address", example = "JohnSmith@example.com")
    String email;

    @Schema(name = "imagePath", description = "Path to image", example = "/Hamlet1131123213.jpg")
    String imagePath;

    @Schema(name = "roles", description = "user roles", example = "[user, admin]")
    @Nullable
    Set<RoleDTO> roles;

    public UserDTO(String name, String surname, String email) {
        this.name = name;
        this.surname = surname;
        this.email = email;
    }
}
