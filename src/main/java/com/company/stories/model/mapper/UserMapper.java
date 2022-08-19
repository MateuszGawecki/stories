package com.company.stories.model.mapper;

import com.company.stories.model.dto.RoleDTO;
import com.company.stories.model.dto.UserDTO;
import com.company.stories.model.entity.Role;
import com.company.stories.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    private final RoleMapper roleMapper;

    @Autowired
    public UserMapper(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }


    public User toUserEntity(UserDTO userDTO){
        return User.builder()
                .user_id(userDTO.getUser_id())
                .name(userDTO.getName())
                .surname(userDTO.getSurname())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .roles(mapToRolesEntity(userDTO.getRoles()))
                .build();
    }

    public UserDTO toUserDTO(User user){
        return UserDTO.builder()
                .user_id(user.getUser_id())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .password(user.getPassword())
                .roles(mapToRolesDTO(user.getRoles()))
                .build();
    }

    private Set<RoleDTO> mapToRolesDTO(Set<Role> roles) {
        return roles.stream()
                .map(roleMapper::toRoleDTO)
                .collect(Collectors.toSet());
    }

    private Set<Role> mapToRolesEntity(Set<RoleDTO> roles) {
        return roles.stream()
                .map(roleMapper::toRoleEntity)
                .collect(Collectors.toSet());
    }
}
