package com.company.stories.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.Set;
@Data
@Builder
@Schema(name = "UserDTO", description = "Representation of user entity")
public class UserRegistrationDTO {

    @Schema(name = "userId", description = "Unique user id", example = "1")
    @Nullable
    Long userId;

    @Schema(name = "name", description = "user name", example = "John")
    String name;

    @Schema(name = "surname", description = "user surname", example = "Smith")
    String surname;

    @Schema(name = "email", description = "Unique user email address", example = "JohnSmith@example.com")
    String email;

    @Schema(name = "password", description = "user password")
    String password;

    @Schema(name = "roles", description = "user roles", example = "[user, admin]")
    @Nullable
    Set<RoleDTO> roles;
}
