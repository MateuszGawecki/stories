package com.company.stories.model.mapper;

import com.company.stories.model.dto.RoleDTO;
import com.company.stories.model.entity.Role;
import org.springframework.stereotype.Component;

@Component
public abstract class RoleMapper {

    public static Role toRoleEntity(RoleDTO roleDTO){
        return Role.builder()
                .role_id(roleDTO.getRole_id())
                .name(roleDTO.getName())
                .build();
    }

    public static RoleDTO toRoleDTO(Role role){
        return RoleDTO.builder()
                .role_id(role.getRole_id())
                .name(role.getName())
                .build();
    }
}
