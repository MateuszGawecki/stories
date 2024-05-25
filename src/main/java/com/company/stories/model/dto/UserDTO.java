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

    UserWithoutDetailsDTO userWithoutDetailsDTO;

    @Schema(name = "email", description = "Unique user email address", example = "JohnSmith@example.com")
    String email;

    @Schema(name = "roles", description = "user roles", example = "[user, admin]")
    @Nullable
    Set<RoleDTO> roles;
}
