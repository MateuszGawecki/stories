package com.company.stories.model.mapper;

import com.company.stories.model.dto.RoleDTO;
import com.company.stories.model.dto.UserDTO;
import com.company.stories.model.entity.Role;
import com.company.stories.model.entity.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public abstract class UserMapper {

    public static User toUserEntity(UserDTO userDTO){
        return User.builder()
                .user_id(userDTO.getUser_id())
                .name(userDTO.getName())
                .surname(userDTO.getSurname())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .roles(mapToRolesEntity(userDTO.getRoles()))
                .build();
    }

    public static UserDTO toUserDTO(User user){
        return UserDTO.builder()
                .user_id(user.getUser_id())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .password(user.getPassword())
                .roles(mapToRolesDTO(user.getRoles()))
                .build();
    }

    private static Set<RoleDTO> mapToRolesDTO(Set<Role> roles) {
        return roles.stream()
                .map(RoleMapper::toRoleDTO)
                .collect(Collectors.toSet());
    }

    private static Set<Role> mapToRolesEntity(Set<RoleDTO> roles) {
        return roles.stream()
                .map(RoleMapper::toRoleEntity)
                .collect(Collectors.toSet());
    }
}
