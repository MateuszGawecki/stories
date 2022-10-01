package com.company.stories.service;

import com.company.stories.exception.RoleNotFoundException;
import com.company.stories.model.dto.RoleDTO;
import com.company.stories.model.entity.Role;
import com.company.stories.model.mapper.RoleMapper;
import com.company.stories.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;


    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<RoleDTO> getAllRoles(){
        List<Role> roles = roleRepository.findAll();

        List<RoleDTO> roleDTOS = roles.stream().map(RoleMapper::toRoleDTO).collect(Collectors.toList());

        return roleDTOS;
    }

    public RoleDTO saveRole(RoleDTO roleDTO){
        Role role = roleRepository.save(RoleMapper.toRoleEntity(roleDTO));

        return RoleMapper.toRoleDTO(role);
    }

    public Role findRoleByName(String roleName){
        Optional<Role> role = roleRepository.findByName(roleName);

        if(role.isEmpty()){
            throw new RoleNotFoundException(String.format("Role %s not found", roleName));
        }

        return role.get();
    }
}
