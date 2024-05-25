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
public class UserWithoutDetailsDTO {

    @Schema(name = "userId", description = "Unique user id", example = "1")
    @Nullable
    Long userId;

    @Schema(name = "name", description = "user name", example = "John")
    String name;

    @Schema(name = "surname", description = "user surname", example = "Smith")
    String surname;

    @Schema(name = "imagePath", description = "Path to image", example = "/Hamlet1131123213.jpg")
    String imagePath;
}

