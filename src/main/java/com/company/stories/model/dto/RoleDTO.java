package com.company.stories.model.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
@Builder
public class RoleDTO {

    @Nullable
    Long role_id;

    String name;
}
