package com.company.stories.model.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoleDTOTest {
    private static final String roleName = "cleaner";
    private static final Long roleId = 1L;

    @Test
    void when_RoleBuilder_expect_Role(){
        //given

        //when
        RoleDTO role = RoleDTO.builder()
                .roleId(roleId)
                .name(roleName)
                .build();

        //then
        assertEquals(roleId, role.getRoleId());
        assertEquals(roleName, role.getName());
    }

    @Test
    void when_setRole_expect_newRoleNameInEntity() {
        //given
        String newRoleName = "driver";

        RoleDTO roleDTO = RoleDTO.builder()
                .roleId(roleId)
                .name(roleName)
                .build();


        //when
        roleDTO.setName(newRoleName);

        //then
        assertEquals(roleId, roleDTO.getRoleId());
        assertEquals(newRoleName, roleDTO.getName());
    }
}
