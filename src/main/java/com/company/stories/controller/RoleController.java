package com.company.stories.controller;

import com.company.stories.model.dto.RoleDTO;
import com.company.stories.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "Endpoints for managing roles - available only for Administrator")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping()
    @Operation(summary = "Getting list of roles defined in db")
    public List<RoleDTO> getAllRoles(){
        return roleService.getAllRoles();
    }

    @PostMapping()
    @Operation(summary = "Creating new role")
    public RoleDTO saveRole(@RequestBody RoleDTO role){
        return roleService.saveRole(role);
    }
}
