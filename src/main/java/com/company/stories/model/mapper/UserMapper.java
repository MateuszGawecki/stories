package com.company.stories.model.mapper;

import com.company.stories.model.dto.RoleDTO;
import com.company.stories.model.dto.UserDTO;
import com.company.stories.model.dto.UserWithoutDetailsDTO;
import com.company.stories.model.entity.Role;
import com.company.stories.model.entity.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public abstract class UserMapper {

    public static User toUserEntity(UserDTO userDTO){
        return User.builder()
                .userId(userDTO.getUserWithoutDetailsDTO().getUserId())
                .name(userDTO.getUserWithoutDetailsDTO().getName())
                .surname(userDTO.getUserWithoutDetailsDTO().getSurname())
                .email(userDTO.getEmail())
                .image_path(userDTO.getUserWithoutDetailsDTO().getImagePath())
                .roles(mapToRolesEntity(userDTO.getRoles()))
                .build();
    }

    public static User toUserEntity(UserWithoutDetailsDTO userDTO){
        return User.builder()
                .userId(userDTO.getUserId())
                .name(userDTO.getName())
                .surname(userDTO.getSurname())
                .image_path(userDTO.getImagePath())
                .build();
    }

    public static UserDTO toUserDTO(User user){
        return UserDTO.builder()
                .email(user.getEmail())
                .roles(mapToRolesDTO(user.getRoles()))
                .build();
    }

    public static UserWithoutDetailsDTO toUserWithoutDetailsDTO(User user){
        return UserWithoutDetailsDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .surname(user.getSurname())
                .imagePath(user.getImage_path())
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
