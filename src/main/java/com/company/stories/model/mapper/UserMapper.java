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
                .user_id(userDTO.getUserId())
                .name(userDTO.getName())
                .surname(userDTO.getSurname())
                .email(userDTO.getEmail())
                .image_path(userDTO.getImagePath())
                .roles(mapToRolesEntity(userDTO.getRoles()))
                .build();
    }

    public static UserDTO toUserDTO(User user){
        return UserDTO.builder()
                .userId(user.getUser_id())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .imagePath(user.getImage_path())
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
