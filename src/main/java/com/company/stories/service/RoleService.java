package com.company.stories.service;

import com.company.stories.exception.RoleNotFoundException;
import com.company.stories.model.entity.Role;
import com.company.stories.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;


    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles(){
        return roleRepository.findAll();
    }

    public Role saveRole(Role role){
        return roleRepository.save(role);
    }

    public Role findRoleByName(String roleName){
        Optional<Role> role = roleRepository.findByName(roleName);

        if(role.isEmpty()){
            throw new RoleNotFoundException(String.format("Role %s not found", roleName));
        }

        return role.get();
    }
}
