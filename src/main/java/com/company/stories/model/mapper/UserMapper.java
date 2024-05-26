package com.company.stories.model.mapper;

import com.company.stories.model.dto.RoleDTO;
import com.company.stories.model.dto.UserDTO;
import com.company.stories.model.dto.UserWithDetailsDTO;
import com.company.stories.model.entity.Role;
import com.company.stories.model.entity.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public abstract class UserMapper {

    public static User toUserEntity(UserDTO userDTO){
        return User.builder()
                .userId(userDTO.getUserId())
                .name(userDTO.getName())
                .surname(userDTO.getSurname())
                .image_path(userDTO.getImagePath())
                .build();
    }

    public static UserDTO toUserDTO(User user){
        return UserDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .surname(user.getSurname())
                .imagePath(user.getImage_path())
                .build();
    }

    public static User toUserEntity(UserWithDetailsDTO userWithDetailsDTO){
        return User.builder()
                .userId(userWithDetailsDTO.getUserDTO().getUserId())
                .name(userWithDetailsDTO.getUserDTO().getName())
                .surname(userWithDetailsDTO.getUserDTO().getSurname())
                .image_path(userWithDetailsDTO.getUserDTO().getImagePath())
                .email(userWithDetailsDTO.getEmail())
                .build();
    }

    public static UserWithDetailsDTO toUserWithDetailsDTO(User user){
        return UserWithDetailsDTO.builder()
                .userDTO(toUserDTO(user))
                .email(user.getEmail())
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
