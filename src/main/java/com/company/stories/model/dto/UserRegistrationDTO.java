package com.company.stories.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(name = "UserDTO", description = "Representation of user entity")
public class UserRegistrationDTO {

    UserDTO userDTO;

    @Schema(name = "email", description = "Unique user email address", example = "  ")
    String email;

    @Schema(name = "password", description = "user password")
    String password;
}
