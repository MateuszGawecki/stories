package com.company.stories.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoleTest {
    private static final String roleName = "cleaner";
    private static final Long roleId = 1L;

    @Test
    void when_RoleBuilder_expect_Role(){
        //given

        //when
        Role role = Role.builder()
                .role_id(roleId)
                .name(roleName)
                .build();

        //then
        assertEquals(roleId, role.getRole_id());
        assertEquals(roleName, role.getName());
    }

    @Test
    void when_setRole_expect_newRoleNameInEntity() {
        //given
        String newRoleName = "driver";

        Role role = Role.builder()
                .role_id(roleId)
                .name(roleName)
                .build();


        //when
        role.setName(newRoleName);

        //then
        assertEquals(roleId, role.getRole_id());
        assertEquals(newRoleName, role.getName());
    }
}
