package com.company.stories.controller;

import com.company.stories.model.dto.RoleDTO;
import com.company.stories.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("get/all")
    public List<RoleDTO> getAllRoles(){
        return roleService.getAllRoles();
    }

    @PostMapping("/save")
    public RoleDTO saveRole(@RequestBody RoleDTO role){
        return roleService.saveRole(role);
    }
}
