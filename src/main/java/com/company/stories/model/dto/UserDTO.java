package com.company.stories.model.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.Set;

@Data
@Builder
public class UserDTO {

    @Nullable
    Long user_id;

    String name;

    String surname;

    String email;

    String password;

    @Nullable
    Set<RoleDTO> roles;
}
