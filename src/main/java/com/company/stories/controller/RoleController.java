package com.company.stories.controller;

import com.company.stories.model.dto.RoleDTO;
import com.company.stories.service.LogService;
import com.company.stories.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "Endpoints for managing roles - available only for Administrator")
public class RoleController {

    private final RoleService roleService;
    private final LogService logService;

    @Autowired
    public RoleController(RoleService roleService, LogService logService) {
        this.roleService = roleService;
        this.logService = logService;
    }

    @GetMapping()
    @Operation(summary = "Getting list of roles defined in db")
    public List<RoleDTO> getAllRoles(){
        return roleService.getAllRoles();
    }

    @PostMapping()
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200")
    })
    @Operation(summary = "Creating new role")
    public RoleDTO saveRole(HttpServletRequest request, @RequestBody RoleDTO role){
        String issuer = ControllerUtils.getIssuer(request);
        logService.saveLog(
                String.format("User %s attempt to create role %s",
                        issuer,
                        role.getName()
                )
        );

        return roleService.saveRole(role);
    }
}
