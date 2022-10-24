package com.company.stories.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
@Builder
@Schema(name = "RoleDTO", description = "Representation of role entity")
public class RoleDTO {

    @Schema(name = "roleId", description = "Unique role id", example = "1")
    @Nullable
    Long roleId;

    @Schema(name = "name", description = "Unique role name", example = "user")
    String name;
}
